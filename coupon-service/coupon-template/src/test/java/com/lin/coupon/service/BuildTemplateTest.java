package com.lin.coupon.service;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.CouponCategory;
import com.lin.coupon.constant.DistributeTarget;
import com.lin.coupon.constant.PeriodType;
import com.lin.coupon.constant.ProductLine;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.vo.TemplateRequest;
import com.lin.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {


    @Autowired
    private BuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws CouponException, InterruptedException {
        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(fakeTemplateRequest())
        ));
        Thread.sleep(3000);
    }

    @Test
    public void test01(){
        System.out.println("测试用例");
    }



    private TemplateRequest fakeTemplateRequest(){
        TemplateRequest request=new TemplateRequest();
        request.setName("优惠卷模板-"+new Date().getTime());
        request.setLogo("http://www.linluo");
        request.setDesc("优惠卷模板");
        request.setCategory(CouponCategory.LIJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10);
        request.setUserId(10001L);
        request.setTarget(DistributeTarget.MULTI.getCode());

        TemplateRule rule=new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(),60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5,1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "广东省","揭阳市",
                JSON.toJSONString(Arrays.asList("文娱","家居"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
