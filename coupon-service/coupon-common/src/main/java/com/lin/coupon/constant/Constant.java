package com.lin.coupon.constant;

/**
 * <h1>通用常量定义</h1>
 */
public class Constant {


    //Kafka消息的Topic
    public static final String TOPIC="lin_user_coupon_op";

    /**
     * <h2>Redis key 前缀定义</h2>
     */
    public static class RedisPrefix{

        //优惠卷码key前缀
        public static final String COUPON_TEMPLATE="lin_coupon_template_code_";

        //用户当前可用所有的优惠卷key前缀
        public static final String USER_COUPON_USABLE="lin_user_coupon_usable_";

        //用户当前所有已使用的优惠卷key前缀
        public static final String USER_COUPON_USED="lin_user_coupon_used_";

        //用户当前所有已过期的优惠卷key前缀
        public static final String USER_COUPON_EXPORED="lin_user_coupon_expired_";
    }


}
