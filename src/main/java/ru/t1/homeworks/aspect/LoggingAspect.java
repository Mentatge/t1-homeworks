package ru.t1.homeworks.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("@annotation(ru.t1.homeworks.aspect.annotation.LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Starting method {}", joinPoint.getSignature().getName());
        log.info("Arguments: {}", joinPoint.getArgs());
    }

    @AfterReturning(value = "@annotation(ru.t1.homeworks.aspect.annotation.LogAfterReturning)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Finish method {}", joinPoint.getSignature().getName());
        log.info("Result: {}", result);
    }

    @AfterThrowing(value = "@annotation(ru.t1.homeworks.aspect.annotation.LogAfterThrowing)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.warn("Method {} thrown exception", joinPoint.getSignature().getName());
        log.error("Arguments: {}", joinPoint.getArgs());
        log.error("Exception: {}", ex.getClass());
    }

    @Around("@annotation(ru.t1.homeworks.aspect.annotation.LogAround)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around start");
        long startTime = System.currentTimeMillis();
        try {
            log.info("Starting method {}", joinPoint.getSignature().getName());
            Object result = joinPoint.proceed();
            log.info("Ending method {}", joinPoint.getSignature().getName());
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error("Around catched exception {}", throwable.getClass());
            throw throwable;
        } finally {
            log.info("Around finish");
            long endTime = System.currentTimeMillis();
            log.info("Time taken: {} ms", endTime - startTime);
        }
    }
}
