package com.lin.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>商品类型枚举</h1>
 */
@AllArgsConstructor
@Getter
public enum  GoodsType {

    WENYU("文娱",1),
    SHENGXIAN("生鲜",2),
    JIAJU("家居",3),
    OTHERS("其他",4),
    ALL("全品类",5);

    private String description;

    private Integer code;

    public static GoodsType of(Integer code){

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean->bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        ()->new IllegalArgumentException(code+"not exist!")
                );
    }
}
