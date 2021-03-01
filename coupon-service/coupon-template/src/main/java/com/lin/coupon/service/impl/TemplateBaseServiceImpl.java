package com.lin.coupon.service.impl;

import com.lin.coupon.dao.CouponTemplateDao;
import com.lin.coupon.entity.CouponTemplate;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.service.TemplateBaseService;
import com.lin.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>优惠卷模板基础服务接口实现</h1>
 */
@Service
@Slf4j
public class TemplateBaseServiceImpl implements TemplateBaseService {

    @Autowired
    private CouponTemplateDao templateDao;


    /**
     * <h2>根据优惠卷模板id 获取优惠卷模板信息</h2>
     * @param id 模板id
     * @return
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {

        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()){
            throw new CouponException("Template Is Not Exist");
        }
        return template.get();
    }


    /**
     * <h2>查找所有可用的优惠卷模板</h2>
     * @return
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {

        List<CouponTemplate> templates = templateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream()
                .map(this::template2TemplateSDK).collect(Collectors.toList());
    }


    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * @param ids
     * @return
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream().map(this::template2TemplateSDK)
                .collect(Collectors.toMap(
                        CouponTemplateSDK::getId, Function.identity() //CouponTemplateSDK本身
                ));
    }

    /**
     * <h2>将CouponTemplate 转换为CouponTemplateSDK</h2>
     * @param template
     * @return
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template){
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(), //并不是拼接好的Template key
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
