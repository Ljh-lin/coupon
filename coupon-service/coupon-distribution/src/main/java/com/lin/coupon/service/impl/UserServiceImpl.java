package com.lin.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.Constant;
import com.lin.coupon.constant.CouponStatus;
import com.lin.coupon.dao.CouponDao;
import com.lin.coupon.entity.Coupon;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.feign.SettlementClient;
import com.lin.coupon.feign.TemplateClient;
import com.lin.coupon.service.IRedisService;
import com.lin.coupon.service.IUserService;
import com.lin.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>用户服务相关的接口实现</h1>
 * 所有操作过程，状态都保存在Redis中，并通过Kafka把消息传递到MySQL中
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private IRedisService redisService;

    //模板微服务客户端
    @Autowired
    private TemplateClient templateClient;

    //结算微服务客户端
    @Autowired
    private SettlementClient settlementClient;

    //kafka客户端
    @Autowired
    private KafkaTemplate<String,String>kafkaTemplate;

    /**
     * <h2>根据用户id和状态查询优惠卷记录</h2>
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {

        List<Coupon> curCached=redisService.getCacheCoupons(userId,status);
        List<Coupon> preTarget;

        if (CollectionUtils.isNotEmpty(curCached)){
            log.debug("coupon cache is not empty:{},{}",userId,status);
            preTarget=curCached;
        }else {
            log.debug("coupon cache is empty,get coupon from db:{},{}",userId,status);
            List<Coupon> dbCoupons=couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));

            //如果数据库没有记录，直接返回即可，Cache中已经加入一张无效的优惠卷
            if (CollectionUtils.isEmpty(dbCoupons)){
                log.debug("current user do not have coupon:{},{}",userId,status);
                return dbCoupons;
            }

            //填充dbCoupons的templateSDK字段
            Map<Integer,CouponTemplateSDK> id2TemplateSDK=
                    templateClient.findIds2TemplateSDk(
                            dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
                    ).getData();
            dbCoupons.forEach(
                    dc->dc.setTemplateSDK(
                            id2TemplateSDK.get(dc.getTemplateId())
                    )
            );
            //数据库中存在记录
            preTarget=dbCoupons;
            //将记录写入Cache
            redisService.addCouponToCache(userId,preTarget,status);
        }

        //将无效优惠卷删除
        preTarget=preTarget.stream()
                .filter(c->c.getId()!=-1)
                .collect(Collectors.toList());
        //如果当前获取的是可用优惠卷，还需对已过期优惠卷的延迟处理
        if (CouponStatus.of(status)==CouponStatus.USABLE){
            CouponClassfiy classfiy=CouponClassfiy.classfiy(preTarget);
            //如果已过期状态不为空,需要做延迟处理
            if (CollectionUtils.isNotEmpty(classfiy.getExpired())){
                log.info("Add Expired Coupons To Cache From FindCouponByStatus: +" +
                        "{},{}",userId,status);
                redisService.addCouponToCache(userId,classfiy.getExpired(),
                        CouponStatus.EXPIRED.getCode());
                //发送到kafka中异步处理
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classfiy.getExpired().stream()
                                    .map(Coupon::getId)
                                    .collect(Collectors.toList())
                        ))
                );
            }
            return classfiy.getUsable();
        }
        return preTarget;
    }


    /**
     * <h2>根据用户id查找当前可以领取的优惠卷模板</h2>
     * @param userId
     * @return
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

        long curTime=new Date().getTime();
        List<CouponTemplateSDK> templateSDKS=templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template(From TemplateClient) Count:{}",templateSDKS.size());

        //过滤过期的优惠卷模板
        templateSDKS=templateSDKS.stream().filter(
                t->t.getRule().getExpiration().getDeadline()>curTime
        ).collect(Collectors.toList());

        log.info("Find Usable Template Count:{}",templateSDKS.size());

        //key是TemplateId
        //value中的left是Template limitation ,right是优惠卷模板
        Map<Integer, Pair<Integer,CouponTemplateSDK>> limit2Template=new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                t->limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(),t)
                )
        );

        List<CouponTemplateSDK> result=new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons=findCouponsByStatus(userId,CouponStatus.USABLE.getCode());

        log.debug("Current User Has Usable Coupons:{},{}",userId,userUsableCoupons.size());

        //key是TemplateId
        //groupingBy() 根据TemplateId作为Map的key，userUsableCoupons作为value
        Map<Integer,List<Coupon>> template2Coupons=userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        //根据Template的Rule判断是否可以领取优惠卷模板
        limit2Template.forEach((k,v)->{

            int limitation=v.getLeft();
            CouponTemplateSDK templateSDK=v.getRight();

            if (template2Coupons.containsKey(k)
                    &&template2Coupons.get(k).size()>=limitation){
                return;
            }
            result.add(templateSDK);
        });

        return result;
    }

    /**
     * <h2>用户领取优惠卷</h2>
     * 1.从TemplateClient拿到对应的优惠卷，并检查是否过期
     * 2.根据limitation判断用户是否可以领取
     * 3.save to db
     * 4.填充CouponTemplateSDK
     * 5.save to cache
     *
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {

        Map<Integer,CouponTemplateSDK> id2Template=
                templateClient.findIds2TemplateSDk(
                        Collections.singletonList(
                                request.getTemplateSDK().getId()
                        )
                ).getData();

        //优惠卷模板需要存在的
        if (id2Template.size()<=0){
            log.error("Can Not Acquire Template From TemplateClient:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }

        //用户是否可以领取优惠卷
        List<Coupon> userUsableCoupons=findCouponsByStatus(request.getUserId(),CouponStatus.USABLE.getCode());
        Map<Integer,List<Coupon>> template2Coupons=userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (template2Coupons.containsKey(request.getTemplateSDK().getId())
                && template2Coupons.get(request.getTemplateSDK().getId()).size()>=
                request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceed Template Assign Limitation:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");

        }

        //尝试获取优惠卷码
        String couponCode=redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId()
        );
        if (StringUtils.isEmpty(couponCode)){
            log.error("Can Not Acquire Coupon Code:{}",request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        Coupon coupon=new Coupon(
                request.getTemplateSDK().getId(),
                request.getUserId(),
                couponCode,
                CouponStatus.USABLE
        );

        coupon=couponDao.save(coupon);
        //填充Coupon对象的CouponTemplateSDK
        coupon.setTemplateSDK(request.getTemplateSDK());

        //放入缓存中
        redisService.addCouponToCache(request.getUserId(),
                Collections.singletonList(coupon),
                CouponStatus.USABLE.getCode());

        return coupon;
    }


    /**
     * <h2>结算(核销)优惠卷</h2>
     * 规则相关处理由Settlement系统去做，当前系统仅仅做业务处理过程(校验过程)
     * @param info
     * @return
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlemet(SettlementInfo info) throws CouponException {

        //当前没有传递优惠卷时，直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos=info.getCouponAndTemplateInfos();
        if (CollectionUtils.isEmpty(ctInfos)){
            log.info("Empty Coupons For Settle");
            double goodsSum=0.0;

            for (GoodsInfo g:info.getGoodsInfos()){
                goodsSum+=g.getPrice()*g.getCount();
            }

            //没有优惠卷也就不存在优惠卷的核销，SettlementInfo其他字段不需要修改
            info.setCost(retain2Decimals(goodsSum));
        }

        //校验传递的优惠卷是否是用户自己的
        List<Coupon> coupons=findCouponsByStatus(info.getUserId(),CouponStatus.USABLE.getCode());
        Map<Integer,Coupon> id2Coupon=coupons.stream()
                .collect(Collectors.toMap(
                        Coupon::getId,
                        Function.identity()
                ));

        if(MapUtils.isEmpty(id2Coupon)|| !CollectionUtils.isSubCollection(
                ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                .collect(Collectors.toList()), id2Coupon.keySet()
        )){
            log.info("{}",id2Coupon.keySet());
            log.info("{}",ctInfos.stream()
                    .map(SettlementInfo.CouponAndTemplateInfo::getId)
                    .collect(Collectors.toList()));

            log.error("User Coupon Has Some Problem，It Is Not SubCollection Of Coupons!");
            throw new CouponException("User Coupon Has Some Problem，It Is Not SubCollection Of Coupons!");
        }

        log.debug("Current Settlement Coupon Is User:{}",ctInfos.size());

        List<Coupon> settleCoupons=new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci->settleCoupons.add(id2Coupon.get(ci.getId())));

        //通过结算微服务获取结算信息
        SettlementInfo processedInfo=
                settlementClient.computeRule(info).getData();
        if (processedInfo.getEmploy()&&CollectionUtils.isNotEmpty(
                processedInfo.getCouponAndTemplateInfos()
        )){
            log.info("Settle User Coupon:{},{}",info.getUserId(),JSON.toJSONString(settleCoupons));
            //更新缓存
            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            //更新db
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId)
                            .collect(Collectors.toList())
                    ))
            );
        }
        return processedInfo;
    }

    /**
     * <h2>保留两位小数</h2>
     * @param value
     * @return
     */
    private double retain2Decimals(double value){

        return new BigDecimal(value)
                .setScale(2,BigDecimal.ROUND_HALF_UP)
                .doubleValue();

    }
}

