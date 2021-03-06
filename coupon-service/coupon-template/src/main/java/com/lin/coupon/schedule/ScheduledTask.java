package com.lin.coupon.schedule;

import com.lin.coupon.dao.CouponTemplateDao;
import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>定时清理已过期的优惠卷模板</h1>
 */
@Slf4j
@Component
public class ScheduledTask {

    @Autowired
    private CouponTemplateDao templateDao;

    /**
     * <h2>下线已过期的优惠卷模板</h2>
     */
    @Scheduled(fixedRate = 60*60*1000)
    public void offlineCouponTemplate(){
        log.info("Start To Expire CouponTemplate");

        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTemplate");
            return;
        }

        Date cur=new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.forEach(t->{
            //根据优惠卷模板规则中的"过期规则"校验模板是否过期
            TemplateRule rule = t.getRule();
            if (rule.getExpiration().getDeadline()<cur.getTime()){
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });

        if (CollectionUtils.isNotEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num:{}",templateDao.saveAll(expiredTemplates));
        }

        log.info("Done To Expire CouponTemplate.");

    }

}
