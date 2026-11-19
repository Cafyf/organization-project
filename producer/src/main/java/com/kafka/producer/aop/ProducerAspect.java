package com.kafka.producer.aop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class ProducerAspect {
    private ObjectMapper objectMapper;

    @Pointcut("execution(* com.kafka.producer.controller.*Controller.*(..))")
    public void producerControllerPointcut() {
    }
    @Around("producerControllerPointcut()")
    public Object logConsumerRequest(
            ProceedingJoinPoint joinPoint) throws Throwable {


        String methodName = joinPoint.getSignature().getName();

        Object[] requestArgs = joinPoint.getArgs();

        log.info("========== Producer controller Request Started ==========");
        log.info("Method Name  : {}", methodName);

        try {
            log.info("Request Payload : {}",
                    objectMapper.writeValueAsString(requestArgs));
        } catch (Exception e) {
            log.warn("Unable to serialize request payload", e);
        }

        long startTime = System.currentTimeMillis();

        try {
            // Execute actual consumer business logic
            Object response = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("controller API method {} was executed successfully",methodName);
            log.info("Execution Time : {} ms", executionTime);

            return response;

        } catch (Throwable exception) {

            log.error("Error:{} | \n  while processing API method: {}", exception,methodName);

            throw exception;

        } finally {
            log.info("========== Consumer Request Completed ==========");
        }
    }
}
