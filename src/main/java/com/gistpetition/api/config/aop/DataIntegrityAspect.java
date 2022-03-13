package com.gistpetition.api.config.aop;

import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
public class DataIntegrityAspect {
    @Around("@annotation(dataIntegrityHandler)")
    public Object handleDataIntegrityException(ProceedingJoinPoint joinPoint, DataIntegrityHandler dataIntegrityHandler) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (DataIntegrityViolationException e) {
            Class<? extends Exception> exceptionClazz = dataIntegrityHandler.value();
            throw exceptionClazz.getDeclaredConstructor().newInstance();
        }
    }
}
