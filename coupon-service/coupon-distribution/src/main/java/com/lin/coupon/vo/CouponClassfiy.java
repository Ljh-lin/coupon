package com.lin.coupon.vo;

import com.lin.coupon.constant.CouponStatus;
import com.lin.coupon.constant.PeriodType;
import com.lin.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>用户优惠卷的分类，根据优惠卷的状态</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassfiy {

    //可以使用的
    private List<Coupon> usable;

    //已使用的
    private List<Coupon> used;

    //已过期的
    private List<Coupon> expired;

    /**
     * <h2>对当前的优惠卷进行分类</h2>
     */
    public static CouponClassfiy classfiy(List<Coupon> coupons){
        List<Coupon> usable=new ArrayList<>(coupons.size());
        List<Coupon> used=new ArrayList<>(coupons.size());
        List<Coupon> expired=new ArrayList<>(coupons.size());

        coupons.forEach(c->{

            //判断优惠卷是否过期
            boolean isTimeExpired;
            long curTime=new Date().getTime();

            //固定的过期时间
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )){
                isTimeExpired=c.getTemplateSDK().getRule().getExpiration()
                        .getDeadline()<=curTime;
            }else {
                //变动过期时间，从领取时间计算
                isTimeExpired= DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime()<=curTime;
            }

            if (c.getStatus()== CouponStatus.USED){
                used.add(c);
            }else if (c.getStatus()==CouponStatus.EXPIRED||isTimeExpired){
                expired.add(c);
            }else {
                usable.add(c);
            }
        });

        return new CouponClassfiy(usable,used,expired);
    }
}
