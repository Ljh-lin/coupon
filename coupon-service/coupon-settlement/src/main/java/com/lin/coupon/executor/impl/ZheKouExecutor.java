package com.lin.coupon.executor.impl;

import com.lin.coupon.constant.RuleFlag;
import com.lin.coupon.executor.AbstractExecutor;
import com.lin.coupon.executor.RuleExecutor;
import com.lin.coupon.vo.CouponTemplateSDK;
import com.lin.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>折扣优惠卷结算规则执行器</h1>
 */
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum=retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos())
        );

        SettlementInfo probability=processGoodsTypeNotStisfy(settlement,goodsSum);
        if (null!=probability){
            log.debug("ZheKou Template Is Not Match GoodsType");
            return probability;
        }

        //折扣优惠卷可以直接使用，没有门槛
        CouponTemplateSDK templateSDK=settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double quota=(double) templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠卷之后的价格
        settlement.setCost(
                retain2Decimals((goodsSum*(quota*1.0/100)))>minCost()?
                        retain2Decimals((goodsSum*(quota*1.0/100))):minCost()
        );
        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum,settlement.getCost());
        return settlement;
    }
}
