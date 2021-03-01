package com.lin.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.lin.coupon.constant.Constant;
import com.lin.coupon.dao.CouponTemplateDao;
import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>异步服务接口实现</h1>
 */
@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private CouponTemplateDao couponTemplateDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据模板异步创建优惠卷码
     * @param template
     */
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch watch=Stopwatch.createStarted();

        Set<String> couponCodes=buildCouponCode(template);

        //lin_coupon_template_code_1
        String rediskey=String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE,template.getId().toString());
        log.info("Push CouponCode To Redis:{}",
                redisTemplate.opsForList().rightPushAll(rediskey,couponCodes));

        //同时设置优惠卷模板可用状态
        template.setAvailable(true);
        couponTemplateDao.save(template);

        watch.stop();
        log.info("Construct CouponCode By Template Cost:{}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        //TODO 发送邮件或短信通知优惠卷已经可用
        log.info("CouponTemplate({}) Is Available!",template.getId());


    }

    /**
     * <h2>构造优惠卷码</h2>
     * 优惠卷码（对应每一张优惠卷，18位）
     * 前四位：产品线+类型
     * 中间六位：日期随机(202010)
     * 后八位：0-9随机数
     * @param template
     * @return
     */
    private Set<String> buildCouponCode(CouponTemplate template){
        Stopwatch watch=Stopwatch.createStarted();
        Set<String> result=new HashSet<>(template.getCount());

        //前四位
        String prefix4=template.getProductLine().getCode().toString()
                +template.getCategory().getCode();
        String date=new SimpleDateFormat("yyyyMMdd").format(template.getCreateTime());

        for (int i = 0; i !=template.getCount() ; i++) {
            result.add(prefix4+buildCouponCodeSuffix14(date));
        }

        assert result.size()==template.getCount();

        watch.stop();
        log.info("Build Coupon Code Cost:{}ms",watch.elapsed(TimeUnit.MILLISECONDS));
        return result;

    }

    private String buildCouponCodeSuffix14(String date){
        char[] bases=new char[]{'1','2','3','4','5','6','7','8','9'};

        //中间6位
        List<Character> chars=date.chars()
                .mapToObj(e->(char)e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6=chars.stream()
                .map(Objects::toString).collect(Collectors.joining());

        //后8位
        String suffix8= RandomStringUtils.random(1,bases)
                +RandomStringUtils.randomNumeric(7);

        return mid6+suffix8;
    }
}
