package com.lin.coupon.service;


import com.lin.coupon.entity.Coupon;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.vo.AcquireTemplateRequest;
import com.lin.coupon.vo.CouponTemplateSDK;
import com.lin.coupon.vo.SettlementInfo;

import java.io.InputStream;
import java.util.List;

/**
 * <h1>用户服务相关的接口定义</h1>
 * 1.用户三类状态优惠卷信息展示服务
 * 2.查看用户当前可以领取的优惠卷模板 -coupon-template微服务配合实现
 * 3.用户领取优惠卷服务
 * 4.用户消费优惠卷服务 -coupon-settlement 微服务配合实现
 */
public interface IUserService {



    /**
     * <h2>根据用户id和状态查询优惠卷记录</h2>
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> findCouponsByStatus(Long userId,Integer status)throws CouponException;

    /**
     * <h2>根据用户id查找当前可以领取的优惠卷模板</h2>
     * @param userId
     * @return
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;


    /**
     * <h2>用户可以领取优惠卷</h2>
     * @return
     */
    Coupon acquireTemplate(AcquireTemplateRequest request)throws CouponException;


    /**
     * <h2>结算(核销)优惠卷</h2>
     * @param info
     * @return
     * @throws CouponException
     */
    SettlementInfo settlemet(SettlementInfo info)throws CouponException;




}
