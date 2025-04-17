package com.leon.adapter;

import com.leon.application.protocol.BaseResponse;
import com.leon.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局业务异常处理
 *
 * @author heyaxing
 */
@Slf4j
@ControllerAdvice
public class BizExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseResponse exceptionHandler(Exception e){
        log.info("其他异常：{}", e.getMessage());
        return BaseResponse.fail("96", "系统异常，请稍后重试");
    }

    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public BaseResponse bizExceptionHandler(BizException e){
        log.info("业务异常：{}", e.getMessage());
        return BaseResponse.fail(e.getRetCode(), e.getMessage());
    }
}
