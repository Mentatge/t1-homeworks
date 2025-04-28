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

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("@annotation(ru.t1.homeworks.aspect.annotation.LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Starting method {}. Arguments: {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "@annotation(ru.t1.homeworks.aspect.annotation.LogAfterReturning)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Finish method {}. Result: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(value = "@annotation(ru.t1.homeworks.aspect.annotation.LogAfterThrowing)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.warn("Exception in {} with args={} → {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()),
                ex.toString(), ex);
    }

    @Around("@annotation(ru.t1.homeworks.aspect.annotation.LogAround)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around start in method {}.", joinPoint.getSignature().getName());
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Around finish.  Time taken: {} ms", endTime - startTime);
        }
    }
}
