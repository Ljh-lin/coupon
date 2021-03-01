package com.lin.coupon.feign;

import com.lin.coupon.exception.CouponException;
import com.lin.coupon.feign.hystrix.SettlementClientHystrix;
import com.lin.coupon.vo.CommonResponse;
import com.lin.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <h1>优惠卷结算微服务 Feign接口定义</h1>
 */
@FeignClient(value = "eureka-client-coupon-settlement",
    fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /**
     * <h2>优惠卷规则计算</h2>
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",
            method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(
            @RequestBody SettlementInfo settlement
    ) throws CouponException;
}
