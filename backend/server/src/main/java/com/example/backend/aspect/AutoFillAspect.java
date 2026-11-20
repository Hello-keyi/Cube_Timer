package com.example.backend.aspect;

import com.example.backend.annotation.AutoFill;
import com.example.backend.context.BaseContext;
import com.example.backend.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.example.backend.mapper.*.*(..)) && @annotation(com.example.backend.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        if (autoFill == null) {
            return;
        }
        OperationType type = autoFill.value();
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            log.warn("自动填充:方法没有参数：{}" , signature.getName());
            return;
        }
        Object entity = args[0];
        if (entity == null) {
            log.warn("自动填充：第一个参数是null：{}" , signature.getName() );
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Integer userId = BaseContext.getContextId();
        if ( type == OperationType.INSERT) {
            setField(entity , "setCreateTime", LocalDateTime.class, now);
            setField(entity, "setCreateUser", Integer.class, userId);
            log.debug("自动填充完成{} - {}" ,entity.getClass().getSimpleName(), type);
        }
        setField(entity, "setUpdateTime", LocalDateTime.class, now);
        setField(entity, "setUpdateUser", Integer.class, userId);
    }
    private void setField(Object entity, String setterName, Class<?> paramType, Object value) {
        try {
            Method method = entity.getClass().getMethod(setterName,paramType);
            method.invoke(entity,value);
        } catch (NoSuchMethodException e) {
            log.warn("自动填充：{} 没有 {} 方法", entity.getClass().getSimpleName(), setterName);
        } catch (Exception e) {
            log.warn("自动填充失败：{} - {}: {}", entity.getClass().getSimpleName(), setterName, e.getMessage());
        }
    }
}