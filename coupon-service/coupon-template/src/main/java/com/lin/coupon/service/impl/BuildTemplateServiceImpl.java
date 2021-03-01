package com.lin.coupon.service.impl;

import com.lin.coupon.dao.CouponTemplateDao;
import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.service.AsyncService;
import com.lin.coupon.service.BuildTemplateService;
import com.lin.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <h1>构建优惠卷模板接口实现</h1>
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements BuildTemplateService {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private CouponTemplateDao templateDao;


    /**
     *<h2>创建优惠卷模板</h2>
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        if (!request.validate()){
            throw new CouponException("BuildTemplate Param Is No valid");
        }

        //判断同名的优惠卷模板是否存在
        if (null!=templateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template!");
        }

        //构造CouponTemplate并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);

        //根据优惠卷模板异步生成优惠卷码
        asyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    /**
     * <h2>将TemplateRequest 转换为CouponTemplate</h2>
     * @return
     */
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
