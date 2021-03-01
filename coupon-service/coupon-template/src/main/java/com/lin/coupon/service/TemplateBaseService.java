package com.lin.coupon.service;

import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠卷模板基础(view,delete...)服务定义</h1>
 */

public interface TemplateBaseService {

    /**
     * <h2>根据优惠卷模板id获取优惠卷模板信息</h2>
     * @param id 模板id
     * @return {@link CouponTemplate}
     */
    CouponTemplate buildTemplateInfo(Integer id)throws CouponException;

    /**
     * <h2>查找所有可用的优惠卷模板</h2>
     * @return
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * <h2>获取模板ids到CouponTemplateSDK的映射</h2>
     * @param ids
     * @return
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
