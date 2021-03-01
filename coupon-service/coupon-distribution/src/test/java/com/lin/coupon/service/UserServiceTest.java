package com.lin.coupon.service;


import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.CouponStatus;
import com.lin.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户服务功能测试用例</h1>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    private Long fakeUserId=1200L;

    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponByStatus() throws CouponException{

        System.out.println(JSON.toJSONString(
                userService.findCouponsByStatus(
                        fakeUserId,
                        CouponStatus.USABLE.getCode()
                )
        ));
    }

    @Test
    public void testFindAvailableTemplate()throws CouponException{
        System.out.println(JSON.toJSONString(
                userService.findAvailableTemplate(fakeUserId)
        ));
    }
}
