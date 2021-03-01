package com.lin.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.CouponCategory;
import com.lin.coupon.constant.RuleFlag;
import com.lin.coupon.executor.AbstractExecutor;
import com.lin.coupon.executor.RuleExecutor;
import com.lin.coupon.vo.GoodsInfo;
import com.lin.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>满减+折扣优惠卷结算规则执行器</h1>
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * <h2>校验商品类型与优惠卷是否匹配</h2>
     * 1.实现满减+折扣优惠卷的校验
     * 2.如果想要使用多类优惠卷，则必须要所有商品类型都包含在内，即差集为空
     * @param settlement
     * @return
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        List<Integer> goodsType=settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType=new ArrayList<>();

        settlement.getCouponAndTemplateInfos().forEach(
                ct->{
                    templateGoodsType.addAll(JSON.parseObject(
                            ct.getTemplate().getRule().getUsage().getGoodsType(),
                            List.class
                    ));
                });

        //如果想要使用多类优惠卷，则必须要所有商品类型都包含在内，即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType,templateGoodsType
        ));
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum=retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));

        //商品校验
        SettlementInfo probability=processGoodsTypeNotStisfy(settlement,goodsSum);
        if (null!=probability){
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manjian=null;
        SettlementInfo.CouponAndTemplateInfo zhekou=null;

        for (SettlementInfo.CouponAndTemplateInfo ct:settlement.getCouponAndTemplateInfos()){
            if (CouponCategory.of(ct.getTemplate().getCategory())==CouponCategory.MANJIAN){
                manjian=ct;
            }else {
                zhekou=ct;
            }
        }

        assert null!=manjian;
        assert null!=zhekou;

        //当前的折扣优惠卷和满减优惠卷如果不能共用，清空优惠卷，返回商品原价
        if (!isTemplateCanShared(manjian,zhekou)){
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfos=new ArrayList<>();
        double manjianBase=(double)manjian.getTemplate().getRule().getDiscount().getBase();
        double maniianQuota=(double)manjian.getTemplate().getRule().getDiscount().getQuota();

        //最终价格
        double targetSum=goodsSum;
        //先计算满减
        if (targetSum>=manjianBase){
            targetSum-=maniianQuota;
            ctInfos.add(manjian);
        }

        //再计算折扣
        double zhekouQuota=(double)zhekou.getTemplate().getRule().getDiscount().getQuota();
        targetSum*=zhekouQuota*1.0/100;
        ctInfos.add(zhekou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(
                targetSum>minCost()?targetSum:minCost()
        ));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());
        return settlement;
    }

    /**
     * <h2>当前两张优惠卷是否可以共用</h2>
     * 校验templateRule 中的weight是否满足条件
     * @param manjian
     * @param zhekou
     * @return
     */
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manjian, SettlementInfo.CouponAndTemplateInfo zhekou) {

        String manjianKey=manjian.getTemplate().getKey()
                +String.format("%04d",manjian.getTemplate().getId());
        String zhekouKey=zhekou.getTemplate().getKey()
                +String.format("%04d",zhekou.getTemplate().getId());

        List<String> allSharedKeysForManjian=new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manjian.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZheKou=new ArrayList<>();
        allSharedKeysForZheKou.add(zhekouKey);
        allSharedKeysForZheKou.addAll(JSON.parseObject(
                zhekou.getTemplate().getRule().getWeight(),
                List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey,zhekouKey),allSharedKeysForManjian)
                || CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey,zhekouKey),allSharedKeysForZheKou);
    }
}
