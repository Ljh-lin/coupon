package com.lin.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.executor.ExecutorManager;
import com.lin.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>结算服务</h1>
 */
@Slf4j
@RestController
public class SettlementController {

    @Autowired
    private ExecutorManager executorManager;

    /**
     * <h2>优惠卷结算</h2>
     *  127.0.0.1:7003/coupon-settlement/settlement/compute
     *  127.0.0.1:9000/lin/coupon-settlement/settlement/compute
     * @param settlement
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement)throws CouponException {
        log.info("settlement:{}", JSON.toJSONString(settlement));

        return executorManager. computeRule(settlement);
    }
}
