package com.lin.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>Http 方法类型枚举</h1>
 */
@Getter
@AllArgsConstructor
public enum  HttpMethodEnum {
    GET,
    HEAD,
    POST,
    PUT,
    PATHC,
    DELETE,
    OPTIONS,
    TRACE,
    ALL


}
