package com.kafka.consumer.aspect;

import com.kafka.consumer.annotation.ServiceName;
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
public class ConsumerAspect {

    private  ObjectMapper objectMapper;
    private static final org. slf4j. Logger loggger
            = org. slf4j. LoggerFactory. getLogger(ConsumerAspect.class);

    // Pointcut: Intercept all methods having @ServiceName annotation
    @Pointcut("@annotation(serviceName)")
    public void serviceNamePointcut(ServiceName serviceName) {
    }

    // Around advice
    @Around("serviceNamePointcut(serviceName)")
    public Object logConsumerRequest(
            ProceedingJoinPoint joinPoint,
            ServiceName serviceName) throws Throwable {

        String service = serviceName.name();

        String methodName = joinPoint.getSignature().getName();

        Object[] requestArgs = joinPoint.getArgs();

        loggger.info("========== Consumer Request Started ==========");
        loggger.info("Service Name : {}", service);
        loggger.info("Method Name  : {}", methodName);

        try {
            loggger.info("Request Payload : {}",
                    objectMapper.writeValueAsString(requestArgs));
        } catch (Exception e) {
            loggger.warn("Unable to serialize request payload", e);
        }

        long startTime = System.currentTimeMillis();

        try {
            // Execute actual consumer business logic
            Object response = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            loggger.info("Service {} executed successfully", service);
            loggger.info("Execution Time : {} ms", executionTime);

            return response;

        } catch (Throwable exception) {

            loggger.error("Error while processing service: {}", service, exception);

            throw exception;

        } finally {
            loggger.info("========== Consumer Request Completed ==========");
        }
    }

}
