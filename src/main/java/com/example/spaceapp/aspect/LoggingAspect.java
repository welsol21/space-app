package com.example.spaceapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AspectJ logging aspect demonstrating 3 pointcuts:
 * 1. Controller layer methods (Before/AfterReturning)
 * 2. Service layer methods (Around)
 * 3. Exception handling (AfterThrowing)
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut 1: Log entry to all controller methods
     */
    @Before("execution(* com.example.spaceapp.controller..*(..))")
    public void logControllerEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("→ Controller Entry: {} with args: {}", methodName, Arrays.toString(args));
    }

    /**
     * Pointcut 1 (continued): Log successful return from controller methods
     */
    @AfterReturning(pointcut = "execution(* com.example.spaceapp.controller..*(..))", returning = "result")
    public void logControllerExit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("← Controller Exit: {} returned: {}", methodName, result);
    }

    /**
     * Pointcut 2: Log service layer execution time (Around advice)
     */
    @Around("execution(* com.example.spaceapp.service..*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.debug("⏱ Service Start: {}", methodName);

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - start;
        log.debug("⏱ Service End: {} | Execution time: {} ms", methodName, elapsedTime);

        return result;
    }

    /**
     * Pointcut 3: Log exceptions thrown from service or controller layers
     */
    @AfterThrowing(pointcut = "execution(* com.example.spaceapp.service..*(..)) || execution(* com.example.spaceapp.controller..*(..))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("💥 Exception in {}: {} - {}", methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }
}
