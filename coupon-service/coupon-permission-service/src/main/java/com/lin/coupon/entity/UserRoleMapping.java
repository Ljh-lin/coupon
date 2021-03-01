package com.lin.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>User 与Role 的映射关系实体类</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coupon_user_role_mapping")
public class UserRoleMapping {

    //主键
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User的主键
    @Basic
    @Column(name = "user_id",nullable = false)
    private Long userId;

    //Role表的主键
    @Basic
    @Column(name = "role_id",nullable = false)
    private Integer roleId;
}
