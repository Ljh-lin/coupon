package com.lin.coupon.executor;

import com.lin.coupon.constant.RuleFlag;
import com.lin.coupon.vo.SettlementInfo;

/**
 * <h1>优惠卷模板规则处理接口定义</h1>
 */
public interface RuleExecutor {

    /**
     * <h2>规则类型标记</h2>
     * @return
     */
    RuleFlag ruleConfig();

    /**
     * <h2>优惠卷规则的计算</h2>
     * @param settlement 包含了选择的优惠卷
     * @return 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
