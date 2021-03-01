package com.lin.coupon.service;


import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.CouponCategory;
import com.lin.coupon.constant.GoodsType;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.executor.ExecutorManager;
import com.lin.coupon.vo.CouponTemplateSDK;
import com.lin.coupon.vo.GoodsInfo;
import com.lin.coupon.vo.SettlementInfo;
import com.lin.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecutorManagerTest {

    private Long fakeUserId=2000L;

    @Autowired
    private ExecutorManager executorManager;

    @Test
    public void testComputeRule() throws CouponException {

//        //满减优惠卷结算测试
//        log.info("Manjian Coupon Executor Test");
//        SettlementInfo manjianInfo=fakeManJianCouponSettlement();
//        SettlementInfo result=executorManager.computeRule(manjianInfo);
//
//        log.info("{}",result.getCost());
//        log.info("{}",result.getCouponAndTemplateInfos().size());
//        log.info("{}",result.getCouponAndTemplateInfos());


        //折扣优惠卷结算测试
//        log.info("ZheKou Coupon Executor Test");
//        SettlementInfo zhekouInfo=fakeZheKouCouponSettlement();
//        SettlementInfo result=executorManager.computeRule(zhekouInfo);
//
//        log.info("{}",result.getCost());
//        log.info("{}",result.getCouponAndTemplateInfos().size());
//        log.info("{}",result.getCouponAndTemplateInfos());

        //立减优惠卷结算测试
//        log.info("LiJian Coupon Executor Test");
//        SettlementInfo lijianInfo=fakeLijianCouponSettlement();
//        SettlementInfo result=executorManager.computeRule(lijianInfo);
//
//        log.info("{}",result.getCost());
//        log.info("{}",result.getCouponAndTemplateInfos().size());
//        log.info("{}",result.getCouponAndTemplateInfos());

        //满减+折扣优惠卷结算测试
        log.info("Two Coupon Executor Test");
        SettlementInfo info=TwoCouponSettlement();
        SettlementInfo result=executorManager.computeRule(info);

        log.info("{}",result.getCost());
        log.info("{}",result.getCouponAndTemplateInfos().size());
        log.info("{}",result.getCouponAndTemplateInfos());



    }


    /**
     * <h2>fake 满减优惠卷的结算信息</h2>
     * @return
     */
    private SettlementInfo fakeManJianCouponSettlement(){

        SettlementInfo info=new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo=new GoodsInfo();
        goodsInfo.setCount(2);
        goodsInfo.setPrice(18.88);
        goodsInfo.setType(GoodsType.SHENGXIAN.getCode());

        GoodsInfo goodsInfo1=new GoodsInfo();
        goodsInfo1.setCount(50);
        goodsInfo1.setPrice(20.88);
        goodsInfo1.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo,goodsInfo1));

        SettlementInfo.CouponAndTemplateInfo ctInfo=new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK=new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        templateSDK.setKey("100120190801");

        TemplateRule rule=new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20,199));
        rule.setUsage(new TemplateRule.Usage("广东省","揭阳市",
                JSON.toJSONString(Arrays.asList(
                GoodsType.SHENGXIAN.getCode(),GoodsType.JIAJU.getCode()))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;

    }

    /**
     * <h2>fake 折扣优惠卷的结算信息</h2>
     * @return
     */
    private SettlementInfo fakeZheKouCouponSettlement(){
        SettlementInfo info=new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo=new GoodsInfo();
        goodsInfo.setCount(2);
        goodsInfo.setPrice(18.88);
        goodsInfo.setType(GoodsType.SHENGXIAN.getCode());

        GoodsInfo goodsInfo1=new GoodsInfo();
        goodsInfo1.setCount(50);
        goodsInfo1.setPrice(20.88);
        goodsInfo1.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo,goodsInfo1));

        SettlementInfo.CouponAndTemplateInfo ctInfo=new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(2);

        CouponTemplateSDK templateSDK=new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK.setKey("888888888");

        TemplateRule rule=new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(80,1));
        rule.setUsage(new TemplateRule.Usage("广东省","揭阳市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.SHENGXIAN.getCode(),GoodsType.JIAJU.getCode()))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;

    }

    /**
     * <h2>fake 立减优惠卷的结算信息</h2>
     * @return
     */
    private SettlementInfo fakeLijianCouponSettlement(){
        SettlementInfo info=new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo=new GoodsInfo();
        goodsInfo.setCount(2);
        goodsInfo.setPrice(18.88);
        goodsInfo.setType(GoodsType.SHENGXIAN.getCode());

        GoodsInfo goodsInfo1=new GoodsInfo();
        goodsInfo1.setCount(50);
        goodsInfo1.setPrice(20.88);
        goodsInfo1.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo,goodsInfo1));

        SettlementInfo.CouponAndTemplateInfo ctInfo=new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(2);

        CouponTemplateSDK templateSDK=new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.LIJIAN.getCode());
        templateSDK.setKey("888888888");

        TemplateRule rule=new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(100,1));
        rule.setUsage(new TemplateRule.Usage("广东省","揭阳市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.SHENGXIAN.getCode(),GoodsType.JIAJU.getCode()))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }


    /**
     * <h2>fake 满减+折扣优惠卷的结算信息</h2>
     * @return
     */
    private SettlementInfo TwoCouponSettlement(){
        SettlementInfo info=new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo=new GoodsInfo();
        goodsInfo.setCount(2);
        goodsInfo.setPrice(18.88);
        goodsInfo.setType(GoodsType.SHENGXIAN.getCode());

        GoodsInfo goodsInfo1=new GoodsInfo();
        goodsInfo1.setCount(50);
        goodsInfo1.setPrice(20.88);
        goodsInfo1.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo,goodsInfo1));

        //满减优惠卷
        SettlementInfo.CouponAndTemplateInfo ctInfo=new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(2);

        CouponTemplateSDK templateSDK=new CouponTemplateSDK();
        templateSDK.setId(12);
        templateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        templateSDK.setKey("5201314115");

        TemplateRule rule=new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(40,100));
        rule.setUsage(new TemplateRule.Usage("广东省","揭阳市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.SHENGXIAN.getCode(),GoodsType.WENYU.getCode()))));
        rule.setWeight(JSON.toJSONString(Collections.emptyList()));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);

        SettlementInfo.CouponAndTemplateInfo zhekouInfo=new SettlementInfo.CouponAndTemplateInfo();
        zhekouInfo.setId(3);
        //折扣优惠卷
        CouponTemplateSDK templateSDK1=new CouponTemplateSDK();
        templateSDK1.setId(10);
        templateSDK1.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK1.setKey("1245111111");
        TemplateRule rule1=new TemplateRule();
        rule1.setDiscount(new TemplateRule.Discount(85,1));
        rule1.setUsage(new TemplateRule.Usage("广东省","揭阳市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.SHENGXIAN.getCode(),GoodsType.JIAJU.getCode()))));
        rule1.setWeight(JSON.toJSONString(Collections.singletonList("52013141150012")));
        templateSDK1.setRule(rule1);
        zhekouInfo.setTemplate(templateSDK1);

        info.setCouponAndTemplateInfos(Arrays.asList(ctInfo,zhekouInfo));

        return info;

    }
}
