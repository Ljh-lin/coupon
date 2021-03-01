package com.lin.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.entity.Coupon;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.service.IUserService;
import com.lin.coupon.vo.AcquireTemplateRequest;
import com.lin.coupon.vo.CouponTemplateSDK;
import com.lin.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>用户服务Controller</h1>
 */
@Slf4j
@RestController
public class UserServiceController {

    @Autowired
    private IUserService userService;

    /**
     * <h2>根据用户id和优惠卷状态查找用户优惠卷记录</h2>
     * 127.0.0.1:7002/coupon-distribution/coupons
     * 127.0.0.1:9000/lin/coupon-distribution/coupons
     * @return
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("status") Integer status)throws CouponException {

        log.info("Find Coupons By Status:{},{}",userId,status);
        return userService.findCouponsByStatus(userId,status);
    }

    /**
     * <h2>根据用户id查找当前可以领取的优惠卷模板</h2>
     * 127.0.0.1:7002/coupon-distribution/template
     * 127.0.0.1:9000/lin/coupon-distribution/template
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam("userId") Long userId) throws CouponException{

        log.info("Find Available Template:{}",userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * <h2>用户领取优惠卷</h2>
     * 127.0.0.1:7002/coupon-distribution/acquire/template
     * 127.0.0.1:9000/lin/coupon-distribution/acquire/template
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)throws CouponException{

        log.info("Acquire Template:{}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);

    }

    /**
     * <h2>结算(核销)优惠卷</h2>
     * 127.0.0.1:7002/coupon-distribution/settlement
     * 127.0.0.1:9000/lin/coupon-distribution/settlement
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info)throws CouponException{

        log.info("Settlement:{}",JSON.toJSONString(info));
        return userService.settlemet(info);
    }
}
