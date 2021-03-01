package com.lin.coupon.filter;


import com.alibaba.fastjson.JSON;
import com.lin.coupon.permission.PermissionClient;
import com.lin.coupon.vo.CheckPermissionRequest;
import com.lin.coupon.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h1>权限过滤器实现</h1>
 */
@Slf4j
@Component
public class PermissionFilter extends AbsSercuritryFilter{

    @Autowired
    private PermissionClient permissionClient;

    @Override
    protected Boolean interceptCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //执行权限校验逻辑
        //从Header中获取到userId
        Long userId=Long.valueOf(request.getHeader("userId"));
        String uri=request.getRequestURI().substring("/lin".length());
        String httpMethod=request.getMethod();

        return permissionClient.checkPermission(new CheckPermissionRequest(userId,uri,httpMethod));

    }

    @Override
    protected int getHttpStatus() {
        return HttpStatus.OK.value();
    }

    @Override
    protected String getErrorMsg() {

        CommonResponse<Object> response=new CommonResponse<>();
        response.setCode(-100);
        response.setMessage("您没有操作权限");

        return JSON.toJSONString(response);
    }
}
