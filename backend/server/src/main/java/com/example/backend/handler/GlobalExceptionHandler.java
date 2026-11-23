package com.example.backend.handler;

import com.example.backend.common.Result;
import com.example.backend.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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


    /**
     * 访问不存在的路径 —— 返回真实的 404
     * 注意：如果不单独处理，会被下面的 Exception 兜底接住，变成「HTTP 200 + 系统繁忙 + ERROR 日志」
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNotFound(NoResourceFoundException e, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        log.warn("接口不存在：/{}", e.getResourcePath());
        return Result.error("接口不存在");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常 ", e);
        return Result.error("系统繁忙");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUpload(MaxUploadSizeExceededException e) {
        log.warn("上传文件过大：{}", e.getMessage());
        return Result.error("文件过大，最大 5MB");
    }
}
