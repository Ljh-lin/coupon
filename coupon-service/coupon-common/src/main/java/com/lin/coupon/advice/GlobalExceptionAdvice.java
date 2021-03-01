package com.lin.coupon.advice;

import com.lin.coupon.enums.CouponExceptionEnum;
import com.lin.coupon.exception.CouponException;
import com.lin.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理</h1>
 * @RestControllerAdvice:组合注解，ControllerAdvice+ResponseBody，是对RestController的功能增强
 *
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest request,CouponException e){
        CommonResponse<String> response = new CommonResponse<>(-1,"business error");
        response.setData(e.getMessage());
        return response;
    }
}
