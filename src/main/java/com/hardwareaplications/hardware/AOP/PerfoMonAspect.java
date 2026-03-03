package com.hardwareaplications.hardware.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PerfoMonAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfoMonAspect.class);

    @Around("execution(* com.hardwareaplications.hardware.Service..*(..)) || execution(* com.hardwareaplications.hardware.Controller..*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint jp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object obj = jp.proceed();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LOGGER.info("Method executed in " + duration + " milliseconds");
        return obj;
    }
}