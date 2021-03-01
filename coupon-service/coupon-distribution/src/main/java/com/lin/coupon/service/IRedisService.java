package com.lin.coupon.service;

import com.lin.coupon.entity.Coupon;
import com.lin.coupon.exception.CouponException;
import java.util.List;

/**
 * <h1>Redis 相关的操作服务接口定义</h1>
 * 1.用户的三个状态优惠卷Cache操作
 * 2.优惠卷模板生成的优惠卷码Cache操作
 */
public interface IRedisService {

    /**
     * <h2>根据userId和状态找到缓存的优惠卷列表数据</h2>
     * @param userId
     * @param status
     * @return 可能返回null，表示从没有过记录
     */
    List<Coupon> getCacheCoupons(Long userId,Integer status);


    /**
     * <h2>保存空的优惠卷列表到缓存中</h2>
     * @param userId
     * @param status 优惠卷状态列表
     */
    void saveEmptyCouponListToCache(Long userId,List<Integer> status);

    /**
     * <h2>尝试从Cache中获取一个优惠卷码</h2>
     * @param templateId
     * @return
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * <h2>将优惠卷保存到Cache中</h2>
     * @param userId
     * @param coupons
     * @param status
     * @return
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId,List<Coupon> coupons,Integer status)throws CouponException;
}
