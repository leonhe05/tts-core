package com.leon.controller;

import com.leon.application.dto.BaseResponse;
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
        return BaseResponse.fail("96", "系统异常");
    }
}
