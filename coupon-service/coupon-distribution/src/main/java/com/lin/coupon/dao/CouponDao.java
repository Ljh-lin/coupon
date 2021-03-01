package com.lin.coupon.dao;

import com.lin.coupon.constant.CouponStatus;
import com.lin.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Coupon Dao 接口定义</h1>
 */
public interface CouponDao extends JpaRepository<Coupon,Integer> {

    /**
     * <h2>根据userId+状态寻找优惠卷记录</h2>
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

}
