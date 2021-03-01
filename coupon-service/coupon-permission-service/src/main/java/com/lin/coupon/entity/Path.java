package com.lin.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>URL 路径信息实体类</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coupon_path")
public class Path {

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;

    //路径模式
    @Basic
    @Column(name = "path_pattern",nullable = false)
    private String pathPattern;

    //HTTP方法类型
    @Basic
    @Column(name = "http_method",nullable = false)
    private String httpMethod;

    //路径描述
    @Basic
    @Column(name = "path_name",nullable = false)
    private String pathName;

    //服务名称
    @Basic
    @Column(name = "service_name",nullable = false)
    private String serviceName;

    //操作类型,READ/WRITE
    @Basic
    @Column(name = "op_mode",nullable = false)
    private String opMode;

    public Path(String pathPattern,String httpMethod,
                String pathName,String serviceName,String opMode){
        this.pathPattern=pathPattern;
        this.httpMethod=httpMethod;
        this.pathName=pathName;
        this.serviceName=serviceName;
        this.opMode=opMode;
    }

}
