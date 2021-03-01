package com.lin.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication8000 {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication8000.class,args);
    }

}
