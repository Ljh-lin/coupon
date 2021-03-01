package com.lin.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>访问日志过滤器</h1>
 */
@Slf4j
@Component
public class AccessLogFIlter extends AbstractPostZuulFiter {
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        //从 PreRequestFilter 获取设置的请求时间戳
        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();
        long duration=System.currentTimeMillis()-startTime;

        //从网关通过的请求都会打印日志记录 ：uri+duration
        log.info("uri:{},duration:{}",uri,duration);
        return success();
    }

    @Override
    public int filterOrder() {

        //网关的所有过滤器前执行完前
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER-1;
    }
}
