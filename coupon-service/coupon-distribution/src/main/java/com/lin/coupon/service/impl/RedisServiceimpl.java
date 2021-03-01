package com.lin.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.Constant;
import com.lin.coupon.constant.CouponStatus;
import com.lin.coupon.entity.Coupon;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>Redis相关操作服务接口实现</h1>
 */
@Slf4j
@Service
public class RedisServiceimpl implements IRedisService {

    /** Redis客户端*/
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * <h2>根据userId和状态找到缓存的优惠卷列表数据</h2>
     * @param userId
     * @param status
     * @return {@link Coupon} 可能返回null，代表从没有过记录
     */
    @Override
    public List<Coupon> getCacheCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache:{},{}",userId,status);
        String redisKey=status2RedisKey(status,userId);

        //序列化后的coupon对象
        List<String> couponStr = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o,null))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStr)){
            saveEmptyCouponListToCache(userId,Collections.singletonList(status));
            return Collections.emptyList();
        }

        //反序列化为Coupon对象
        return couponStr.stream()
                .map(cs-> JSON.parseObject(cs,Coupon.class))
                .collect(Collectors.toList());
    }


    /**
     * <h2>保存空的优惠卷列表到缓存中</h2>
     * 目的：避免缓存穿透
     * 解决：填充-1无效的优惠卷
     * @param userId
     * @param status 优惠卷状态列表
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {

        log.info("Save Empty List To Cache For User:{},Status:{}",
                userId,JSON.toJSONString(status));

        //key是coupon_id,value是序列化的Coupon
        Map<String,String> invaldCouponMap=new HashMap<>();
        invaldCouponMap.put("-1",JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠卷缓存信息
        //KV K:status->redisKey V:{coupon_id:序列化的Coupon}
        //使用SessionCallback把数据命令放入到Redis的pipeline
        SessionCallback<Object> sessionCallback=new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                status.forEach(s->{
                    String redisKey=status2RedisKey(s,userId);
                    operations.opsForHash().putAll(redisKey,invaldCouponMap);
                });
                return null;
            }
        };

        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }


    /**
     * <h2>尝试从Cache中获取一个优惠卷码</h2>
     * @param templateId 优惠卷模板主键
     * @return 优惠卷码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {

        String redisKey=String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,templateId.toString());
        //优惠卷码不存在顺序关系，左边pop或右边pop，没有影响
        String couponCode=redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code:{},{},{}",templateId,redisKey,couponCode);

        return couponCode;
    }


    /**
     * <h2>将优惠卷保存到Cache中</h2>
     * @param userId
     * @param coupons
     * @param status
     * @return 保存成功个数
     * @throws CouponException
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{},{},{}",
                userId,JSON.toJSONString(coupons),status);

        Integer result=-1;
        CouponStatus couponStatus=CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                result=addCouponToCacheForUsable(userId,coupons);
                break;
            case USED:
                result=addCouponToCacheForUsed(userId,coupons);
                break;
            case EXPIRED:
                result=addCouponToCacheForExpired(userId,coupons);
                break;
        }
        return result;
    }


    /**
     * <h2>将过期优惠卷加入到Cache中</h2>
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException {

        //status是EXPIRED，代表是已有的优惠卷过期了，影响两个Cache
        //USABLE，EXPIRED

        log.debug("Add Coupon To Cache For Expired");

        //最终需要保存的Cache
        Map<String,String> needCacheForExpired=new HashMap<>(coupons.size());

        String redisForUsable=status2RedisKey(CouponStatus.USABLE.getCode(),userId);
        String redisForExpired=status2RedisKey(CouponStatus.EXPIRED.getCode(),userId);

        List<Coupon> curUsableCoupons=getCacheCoupons(userId,CouponStatus.USABLE.getCode());

        //可用的优惠卷个数一定大于1
        assert curUsableCoupons.size()>coupons.size();

        coupons.forEach(c->needCacheForExpired.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        //校验当前的优惠卷参数是否与Cache中的匹配
        List<Integer> curUsableIds=curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds=coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("CurCoupons Is Not Equal To Cache:{},{},{}",
                    userId,JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache");
        }

        List<String> needCleanKey=paramIds.stream()
                .map(i->i.toString()).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback=new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {

                //1.已过期的优惠卷Cache缓存
                operations.opsForHash().putAll(
                        redisForExpired,needCacheForExpired
                );
                //2.可用的优惠卷Cache需要清理
                operations.opsForHash().delete(
                        redisForUsable,needCleanKey.toArray()
                );
                //3.重置过期时间
                operations.expire(
                        redisForUsable,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                operations.expire(
                        redisForExpired,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };

        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

      return coupons.size();
    }


    /**
     * <h2>将已使用的优惠卷加入到Cache中</h2>
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {

        //如果status是USED,代表用户操作是使用当前的优惠卷，影响到两个Cache
        //USABLE ,USED

        log.debug("Add Coupon To Cache For Used");
        Map<String,String> needCacheForUsed=new HashMap<>(coupons.size());

        String redisKeyForUsable=status2RedisKey(CouponStatus.USABLE.getCode(),userId);
        String redisKeyForUsed=status2RedisKey(CouponStatus.USED.getCode(),userId);

        //获取当前用户可用的优惠卷
        List<Coupon> curUsableCoupons=getCacheCoupons(userId,CouponStatus.USABLE.getCode());

        //当前可用的优惠卷个数一定是大于1的，包括无效的优惠卷
        assert curUsableCoupons.size()>coupons.size();

        coupons.forEach(c->needCacheForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        //校验当前的优惠卷参数是否与Cache中的匹配
        List<Integer>curUsableIds=curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramsIds=coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());


        //paramsIds是curUsableIds的子集
        if (!CollectionUtils.isSubCollection(paramsIds,curUsableIds)){
            log.error("CurCoupons Is Not Equal ToCache:{},{},{}",
                    userId,JSON.toJSONString(curUsableIds),
                    JSON.toJSON(paramsIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        List<String> needCleanKey=paramsIds.stream()
                .map(i->i.toString()).collect(Collectors.toList());
        SessionCallback<Object> sessionCallback=new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                //1.已使用的优惠卷Cache缓存添加
                operations.opsForHash().putAll(
                        redisKeyForUsed,needCacheForUsed
                );
                //2.可用的优惠卷Cache需要清理
                operations.opsForHash().delete(
                        redisKeyForUsable,needCleanKey.toArray()
                );
                //3.重置过期时间
                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                operations.expire(
                        redisKeyForUsed,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };

        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();
    }


    /**
     * <h2>新增加优惠卷到Cache中</h2>
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {

        //status是USABLE，代表是新增的优惠卷
        //只会影响一个Cache：USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable");

        //优惠卷信息
        Map<String,String> needCacheObject=new HashMap<>();
        coupons.forEach(c->
                needCacheObject.put(
                        c.getId().toString(),
                        JSON.toJSONString(c)
                ));

        String redisKey=status2RedisKey(CouponStatus.USABLE.getCode(),userId);
        redisTemplate.opsForHash().putAll(redisKey,needCacheObject);
        log.info("Add {} Coupons To Cache:{},{}",
                needCacheObject.size(),userId,redisKey);

        redisTemplate.expire(
                redisKey,
                getRandomExpirationTime(1,2),
                TimeUnit.SECONDS
        );

        return needCacheObject.size();
    }


    /**
     * <h2>根据status获取对应的Redis Key</h2>
     */
    private String status2RedisKey(Integer status, Long userId) {

        String redisKey=null;
        CouponStatus couponStatus=CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                redisKey=String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE,userId);
                break;
            case USED:
                redisKey=String.format("%s%s",Constant.RedisPrefix.USER_COUPON_USED,userId);
                break;
            case EXPIRED:
                redisKey=String.format("%s%s",Constant.RedisPrefix.USER_COUPON_EXPORED,userId);
                break;
        }
        return redisKey;
    }

    /**
     * <h2>获取一个随机的过期时间</h2>
     * 缓存雪崩：key在同一时间失效
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return
     */
    private Long getRandomExpirationTime(Integer min,Integer max){

        return RandomUtils.nextLong(
                min*60*60,
                max*60*60
        );

    }
}
