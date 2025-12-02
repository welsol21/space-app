package com.example.spaceapp.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Controllers
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
    }

    // Services
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {
    }

    // Exception handlers
    @Pointcut("within(@org.springframework.web.bind.annotation.ControllerAdvice *)")
    public void exceptionHandlerPointcut() {
    }

    @Around("restControllerPointcut()")
    public Object logAroundControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.info("REST controller call: {}", method);
        try {
            Object result = joinPoint.proceed();
            log.info("REST controller completed: {}", method);
            return result;
        } catch (Throwable ex) {
            log.error("REST controller exception in {}: {}", method, ex.getMessage());
            throw ex;
        }
    }

    @Around("servicePointcut()")
    public Object logAroundServices(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.info("Service call: {}", method);
        try {
            Object result = joinPoint.proceed();
            log.info("Service completed: {}", method);
            return result;
        } catch (Throwable ex) {
            log.error("Service exception in {}: {}", method, ex.getMessage());
            throw ex;
        }
    }

    @AfterThrowing(pointcut = "restControllerPointcut() || servicePointcut()", throwing = "ex")
    public void logThrownExceptions(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception thrown in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }

    @Around("exceptionHandlerPointcut()")
    public Object logExceptionHandlers(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.info("Handling exception in {}", method);
        return joinPoint.proceed();
    }
}
