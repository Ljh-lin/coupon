package com.lin.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @EnableZuulProxy 标识当前是Zuul Server
 *  @SpringCloudApplication 组合了 SpringBoot 应用 + 服务发现 + 熔断
 */
@EnableFeignClients
@EnableZuulProxy
@SpringCloudApplication
public class ZuulApplication9000 {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication9000.class,args);
    }
}
