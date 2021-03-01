package com.lin.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>优惠卷分类</h1>
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {

    MANJIAN("满减卷","001"),
    ZHEKOU("折扣卷","002"),
    LIJIAN("立减卷","003");


    //优惠卷描述(分类)
    private String description;

    //优惠卷分类编号
    private String code;

    public static CouponCategory of(String code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean->bean.code.equals(code))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException(code+"not exist!"));
    }

}
