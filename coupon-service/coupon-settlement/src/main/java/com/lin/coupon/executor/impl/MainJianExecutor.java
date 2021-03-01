package com.lin.coupon.executor.impl;


import com.lin.coupon.constant.RuleFlag;
import com.lin.coupon.executor.AbstractExecutor;
import com.lin.coupon.executor.RuleExecutor;
import com.lin.coupon.vo.CouponTemplateSDK;
import com.lin.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * <h1>满减优惠卷结算规则执行器</h1>
 */
@Slf4j
@Component
public class MainJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * <h2>规则类型标记</h2>
     * @return
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * <h2>优惠卷规则的计算</h2>
     * @param settlement 包含了选择的优惠卷
     * @return
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodSum=retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));
        SettlementInfo probability=processGoodsTypeNotStisfy(settlement,goodSum);

        //没有匹配上优惠卷
        if (null!=probability){
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK=settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double base=(double)templateSDK.getRule().getDiscount().getBase();
        double quota=(double) templateSDK.getRule().getDiscount().getQuota();

        //如果不符合标准，直接返回商品总价
        if (goodSum<base){
            log.debug("Current Goods Sum<ManJian Coupon Base!");
            settlement.setCost(goodSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        //计算使用优惠卷之后的价格-结算
        settlement.setCost(retain2Decimals(
                (goodSum-quota)>minCost()?(goodSum-quota):minCost()
        ));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}",
                goodSum,settlement.getCost());
        return settlement;
    }
}