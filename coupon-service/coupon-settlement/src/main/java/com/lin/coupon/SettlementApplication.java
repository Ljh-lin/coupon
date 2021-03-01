package com.lin.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * <h1>优惠卷结算微服务启动入口</h1>
 */

@SpringBootApplication
@EnableEurekaClient
public class SettlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(SettlementApplication.class,args);
    }
}
