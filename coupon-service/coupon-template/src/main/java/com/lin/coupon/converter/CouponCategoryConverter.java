package com.lin.coupon.converter;

import com.lin.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * <h1>优惠卷分发枚举属性转换器</h1>
 * AttributeConverter<X,Y>
 *   X:实体属性类型
 *   Y:数据库字段类型
 */
@Converter
public class CouponCategoryConverter
        implements AttributeConverter<CouponCategory,String> {


    /**
     *<h2>将实体属性X转换为Y存储到数据库中</h2>
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     *<h2>将数据库中的字段Y转换为实体属性X</h2>
     */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
