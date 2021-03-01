package com.lin.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>权限描述注解：定义Controller接口的权限</h1>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CouponPermission {
    //接口描述信息
    String description() default "";

    //此接口是否为只读，默认是true
    boolean readOnly() default true;

    //扩展属性，最好以JSON格式存储
    String extra() default "";
}
