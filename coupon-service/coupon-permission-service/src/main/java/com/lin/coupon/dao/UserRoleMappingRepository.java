package com.lin.coupon.dao;

import com.lin.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>UserRoleMapping Dao</h1>
 */
public interface UserRoleMappingRepository
        extends JpaRepository<UserRoleMapping,Long> {

    /**
     * <h2>通过userId寻找数据记录</h2>
     * @param userId
     * @return
     */
    UserRoleMapping findByUserId(Long userId);
}

