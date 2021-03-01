package com.lin.coupon.service;

import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.vo.TemplateRequest;

/**
 * <h1>构建优惠卷模板接口定义</h1>
 */
public interface BuildTemplateService {

    CouponTemplate buildTemplate(TemplateRequest request)throws CouponException;

}
