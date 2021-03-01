package com.lin.coupon.service;

import com.lin.coupon.entity.CouponTemplate;

/**
 * <h1>异步服务接口定义</h1>
 */
public interface AsyncService {

    /**
     * <h2>根据模板异步的创建优惠卷码</h2>
     * @param template
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
