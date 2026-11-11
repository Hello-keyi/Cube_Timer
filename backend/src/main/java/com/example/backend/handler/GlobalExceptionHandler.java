package com.example.backend.handler;

import com.example.backend.common.Result;
import com.example.backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常 {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("参数缺失 {}", e.getParameterName());
        return Result.error("缺少必填参数：" + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        if (fe == null) {
            return Result.error("参数错误" );
        }

        log.warn( "参数错误 字段 ：{} 。信息： {}。" , fe.getField(), fe.getDefaultMessage() );
        return Result.error(fe.getDefaultMessage() );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleBody(HttpMessageNotReadableException e) {
        log.warn("请求体错误 {}", e.getMessage());
        return Result.error("请求体错误");
    }


    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常 ", e);
        return Result.error("系统繁忙");
    }
}
