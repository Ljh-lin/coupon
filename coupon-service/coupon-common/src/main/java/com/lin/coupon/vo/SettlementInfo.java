package com.lin.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>结算信息对象定义</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementInfo {

    /** 用户id*/
    private Long userId;

    /** 商品信息*/
    private List<GoodsInfo> goodsInfos;

    /** 优惠卷列表*/
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    /** 是否使结算生效，即核销*/
    private Boolean employ;

    /** 结算金额*/
    private Double cost;

    /**
     * <h2>优惠卷和模板信息</h2>
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CouponAndTemplateInfo{

        /** Coupon的主键*/
        private Integer id;

        /** 优惠卷对应的模板对象*/
        private CouponTemplateSDK template;

    }
}
