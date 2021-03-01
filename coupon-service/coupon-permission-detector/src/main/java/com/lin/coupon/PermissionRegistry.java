package com.lin.coupon;

import com.lin.coupon.permission.PermissionClient;
import com.lin.coupon.vo.CommonResponse;
import com.lin.coupon.vo.CreatePathRequest;
import com.lin.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>权限注册组件</h1>
 */
@Slf4j
public class PermissionRegistry {

    //权限服务SDK客户端
    private PermissionClient permissionClient;

    //服务名称
    private String serviceName;

    PermissionRegistry(PermissionClient permissionClient,String serviceName){
        this.permissionClient=permissionClient;
        this.serviceName=serviceName;
    }

    /**
     * <h2>权限注册</h2>
     */
    boolean register(List<PermissionInfo> infoList){
        if (CollectionUtils.isEmpty(infoList)){
            return false;
        }
        List<CreatePathRequest.PathInfo> pathInfos=infoList.stream()
                .map(info->CreatePathRequest.PathInfo.builder()
                .pathPattern(info.getUrl())
                    .httpMethod(info.getMethod())
                    .pathName(info.getDescription())
                    .serviceName(serviceName)
                    .opMode(info.getIsRead()?OpModeEnum.READ.name():OpModeEnum.WRITE.name())
                    .build()
                ).collect(Collectors.toList());

        CommonResponse<List<Integer>> response=permissionClient.createPath(
                new CreatePathRequest(pathInfos)
        );

        if (!CollectionUtils.isEmpty(response.getData())){
            log.info("register path info:{}",response.getData());
            return true;
        }
        return false;

    }
}
