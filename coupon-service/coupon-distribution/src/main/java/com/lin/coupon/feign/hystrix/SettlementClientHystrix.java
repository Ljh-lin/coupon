package com.lin.coupon.feign.hystrix;

import com.lin.coupon.exception.CouponException;
import com.lin.coupon.feign.SettlementClient;
import com.lin.coupon.vo.CommonResponse;
import com.lin.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>结算微服务熔断策略实现</h1>
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    /**
     * <h2>优惠券规则计算</h2>
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException {

        log.error("[eureka-client-coupon-settlement] computeRule request error");

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] request error ",
                settlement
        );
    }
}
