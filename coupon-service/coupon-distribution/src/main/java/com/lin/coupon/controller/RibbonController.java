package com.lin.coupon.controller;

import com.lin.coupon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * <h1>Ribbon应用Controller</h1>
 */
@Slf4j
@RestController
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * <h2>通过Ribbon组件调用模板微服务</h2>
     * @return
     */
    @GetMapping("/info")
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo(){

        String infoUrl="http://eureka-client-coupon-template"+
                "/coupon-template/info";
        return restTemplate.getForEntity(infoUrl,TemplateInfo.class).getBody();
    }

    /**
     * <h2>模板微服务的元信息</h2>
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TemplateInfo{
        private Integer code;
        private String message;
        private List<Map<String,Object>>data;
    }
}
