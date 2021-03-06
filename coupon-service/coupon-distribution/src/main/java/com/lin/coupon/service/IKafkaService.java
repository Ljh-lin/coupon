package com.lin.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * <h1>Kafka 相关的服务接口定义</h1>
 */
public interface IKafkaService {

    /**
     *<h2>消费优惠卷Kafka消息</h2>
     */
    public void consumeCouponKafkaMessage(ConsumerRecord<?,?> record);
}
