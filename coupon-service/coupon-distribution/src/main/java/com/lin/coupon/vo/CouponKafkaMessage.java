package com.lin.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>优惠卷kafka消息对象定义</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {

    //优惠卷状态
    private Integer status;

    //Coupon主键
    private List<Integer> ids;
}
