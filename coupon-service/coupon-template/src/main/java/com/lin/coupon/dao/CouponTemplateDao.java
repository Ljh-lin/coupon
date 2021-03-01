package com.lin.coupon.dao;

import com.lin.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  <h1>CouponTemplateDao 接口定义</h1>
 */

public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {

    /**
     * <h2>根据模板名称查看模板</h2>
     */
    CouponTemplate findByName(String name);

    /**
     * <h2>根据available和expired查找模板记录</h2>
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available,Boolean expired);

    /**
     * <h2>根据expired 标记查找模板记录</h2>
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
