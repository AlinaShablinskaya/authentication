package com.example.authentication.aspect.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Aspect
@Component
public class ServiceLogAspect extends BaseLogAspect {
    @Pointcut("execution(* com.example.authentication.services..*(..)) " +
            "&& !@annotation(com.example.authentication.aspect.log.ExcludeLog))")
    public void isServiceLayer() {
    }

    @Before("isServiceLayer()")
    public void logServiceBefore(JoinPoint joinPoint) {
        log.info(BEFORE_SERVICE_PATTERN,
                joinPoint.getSignature().toShortString(),
                getArgsWithName(joinPoint));
    }

    @AfterReturning(pointcut = "isServiceLayer()", returning = "result")
    public void logServiceAfter(JoinPoint joinPoint, Object result) {
        log.info(AFTER_SERVICE_PATTERN,
                joinPoint.getSignature().toShortString(),
                getStringInstanceOf(Optional.ofNullable(result).orElse("")),
                getArgsWithName(joinPoint));
    }

    @AfterThrowing(pointcut = "isServiceLayer()", throwing = "e")
    public void logServiceAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error(AFTER_THROWING_PATTERN,
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage());
    }
}
