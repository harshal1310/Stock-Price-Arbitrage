package com.broker.arbitrage.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckLogin {
    @Around("@annotation(com.broker.arbitrage.CustomAnnotations.CheckLogin)")
    public Object CheckLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before method execution");
        Object result = joinPoint.proceed(); // executes the actual method
        System.out.println("After method execution");
        return result;

    }
}
