package com.lin.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.lin.coupon.annotation.CouponPermission;
import com.lin.coupon.annotation.IgnorePermission;
import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.service.BuildTemplateService;
import com.lin.coupon.service.TemplateBaseService;
import com.lin.coupon.vo.CouponTemplateSDK;
import com.lin.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠卷模板相关功能控制器</h1>
 */
@Slf4j
@RestController
public class CouponTemplateController {

    @Resource
    private BuildTemplateService buildTemplateService;

    @Resource
    private TemplateBaseService templateBaseService;

    /**
     * <h2>构建优惠卷模板</h2>
     *  127.0.0.1:7001/coupon-template/template/build
     *  127.0.0.1:9000/lin/coupon-template/template/build
     */
    @PostMapping("/template/build")
    @CouponPermission(description = "buildTemplate",readOnly = false)
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template:{}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * <h2>构建优惠卷模板详情</h2>
     *127.0.0.1:7001/coupon-template/template/info?id=1
     *127.0.0.1:9000/lin/coupon-template/template/info?id=1
     */

    @GetMapping("/template/info")
    @CouponPermission(description = "buildTemplateInfo")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id)throws CouponException{
        log.info("Build Template Info For:{}",id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可用的优惠卷模板</h2>
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * 127.0.0.1:9000/lin/coupon-template/template/sdk/all
     */

    @GetMapping("/template/sdk/all")
    @IgnorePermission
    public List<CouponTemplateSDK> findAllUsableTemplate(){
        log.info("Find All Usable Template");
        return templateBaseService.findAllUsableTemplate();
    }


    /**
     * <h2>获取模板ids 到CouponTemplateSDK 的映射</h2>
     *  127.0.0.1:7001/coupon-template/template/sdk/infos
     *  127.0.0.1:9000/lin/coupon-template/template/sdk/infos?ids=1,2
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer,CouponTemplateSDK> findIdsTemplateSDK(@RequestParam("ids")Collection<Integer> ids){
        log.info("FindIds2TemplateSDK:{}",JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
