package com.lin.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.constant.Constant;
import com.lin.coupon.constant.CouponStatus;
import com.lin.coupon.dao.CouponDao;
import com.lin.coupon.entity.Coupon;
import com.lin.coupon.service.IKafkaService;
import com.lin.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * <h1> kafka相关的服务接口实现</h1>
 * 将Cache中的Coupon的状态变化同步到DB中
 */
@Slf4j
@Component
public class KafkaServiceImpl implements IKafkaService {

    @Autowired
    private CouponDao couponDao;

    /**
     * <h2>消费优惠卷Kafka消息</h2>
     * @param record
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC},groupId = "lin-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        Optional<?>kafkaMessage=Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){
            Object messgae=kafkaMessage.get();
            CouponKafkaMessage couponInfo= JSON.parseObject(
                    messgae.toString(),
                    CouponKafkaMessage.class
            );

            log.info("Receive CouponKafkaMessage:{}",messgae.toString());

            CouponStatus status=CouponStatus.of(couponInfo.getStatus());

            switch (status){
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo,status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo,status);
                    break;
            }

        }

    }

    /**
     * <h2>处理已过期的用户优惠卷</h2>
     */
    private void processExpiredCoupons(CouponKafkaMessage couponInfo, CouponStatus status) {

        processCouponByStatus(couponInfo,status);

    }

    /**
     * <h2>处理已使用的用户优惠卷</h2>
     */
    private void processUsedCoupons(CouponKafkaMessage couponInfo, CouponStatus status) {
        processCouponByStatus(couponInfo,status);
    }

    /**
     * <h2>根据状态处理优惠卷信息</h2>
     */
    private void processCouponByStatus(CouponKafkaMessage kafkaMessage,CouponStatus status){
        List<Coupon> coupons=couponDao.findAllById(kafkaMessage.getIds());

        if (CollectionUtils.isEmpty(coupons)||
                coupons.size()!=kafkaMessage.getIds().size()){
            log.error("Can Not Find Right Coupon Info:{}",
                    JSON.toJSONString(kafkaMessage));
            //TODO 发送邮件
            return;
        }
        coupons.forEach(c->c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count:{}",
                couponDao.saveAll(coupons).size());
    }
}
