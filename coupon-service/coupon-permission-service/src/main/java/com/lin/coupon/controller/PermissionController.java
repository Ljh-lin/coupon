package com.lin.coupon.controller;

import com.lin.coupon.annotation.IgnoreResponseAdvice;
import com.lin.coupon.service.PathService;
import com.lin.coupon.service.PermissionService;
import com.lin.coupon.vo.CheckPermissionRequest;
import com.lin.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1>路径创建与权限校验对外服务接口</h1>
 */
@Slf4j
@RestController
public class PermissionController {

    @Autowired
    private PathService pathService;
    @Autowired
    private PermissionService permissionService;

    /**
     * <h2>路径创建接口</h2>
     * @param request
     * @return
     */
    @PostMapping("/create/path")
    public List<Integer> createPath(@RequestBody CreatePathRequest request){
        log.info("createPath:{}",request.getPathInfos().size());
        return pathService.createPath(request);
    }

    /**
     * <h2>权限校验接口</h2>
     * @param request
     * @return
     */
    @IgnoreResponseAdvice
    @PostMapping("/check/permission")
    public Boolean checkPermission(@RequestBody CheckPermissionRequest request){
        log.info("checkPermission for args:{},{},{}",
                request.getUserId(),request.getUri(),request.getHttpMethod());
        return permissionService.checkPermission(
                request.getUserId(),request.getUri(),request.getHttpMethod()
        );

    }


}
