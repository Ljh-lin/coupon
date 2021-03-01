package com.lin.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>role与path映射信息实体类</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coupon_role_path_mapping")
public class RolePathMapping {

    //主键
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Role表的主键
    @Basic
    @Column(name = "role_id",nullable = false)
    private Integer roleId;

    //Path表的主键
    @Basic
    @Column(name = "path_id",nullable = false)
    private Integer pathId;

}
