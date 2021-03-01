package com.lin.coupon.enums;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CouponExceptionEnum {
    BUSINESSERROR(-1,"business error");

    private Integer code;
    private String info;

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public static CouponExceptionEnum codeOf(int code){
        for (CouponExceptionEnum couponExceptionEnum:values()){
            if (couponExceptionEnum.getCode()==code){
                return couponExceptionEnum;
            }
        }
        return null;
    }
}
