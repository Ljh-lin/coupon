package com.lin.coupon.service;

import com.lin.coupon.constant.RoleEnum;
import com.lin.coupon.dao.PathRepository;
import com.lin.coupon.dao.RolePathMappingRepository;
import com.lin.coupon.dao.RoleRepository;
import com.lin.coupon.dao.UserRoleMappingRepository;
import com.lin.coupon.entity.Path;
import com.lin.coupon.entity.Role;
import com.lin.coupon.entity.RolePathMapping;
import com.lin.coupon.entity.UserRoleMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <h1>权限校验功能服务接口</h1>
 */
@Service
@Slf4j
public class PermissionService {

    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;
    @Autowired
    private RolePathMappingRepository rolePathMappingRepository;


    /**
     * <h2>用户访问接口权限校验</h2>
     * @param userId 用户id
     * @param uri 访问uri
     * @param httpMethod 请求类型
     * @return true/false
     */
    public Boolean checkPermission(Long userId,String uri,String httpMethod){

        UserRoleMapping userRoleMapping = userRoleMappingRepository.findByUserId(userId);

        //如果用户角色映射表找不到记录，直接返回false
        if (null==userRoleMapping){
            log.error("userId not exist is UserRoleMapping:{}",userId);
            return false;
        }

        //如果找不到对应的Role记录，直接返回false
        Optional<Role> role = roleRepository.findById(userRoleMapping.getRoleId());
        if (!role.isPresent()){
            log.error("roleId not exist in Role:{}",userRoleMapping.getRoleId());
            return false;
        }

        //如果用户角色是超级管理员，直接返回true
        if (role.get().getRoleTag().equals(RoleEnum.SUPER_ADMIN.name())){
            return true;
        }

        //如果路径没有注册(忽略了)，直接返回true，监控接口不需要权限
        Path path = pathRepository.findByPathPatternAndHttpMethod(uri, httpMethod);
        if (null==path){
            return true;
        }

        RolePathMapping rolePathMapping = rolePathMappingRepository.findByRoleIdAndPathId(role.get().getId(), path.getId());

        return rolePathMapping!=null;


    }
}
