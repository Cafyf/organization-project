package com.kafka.consumer.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceName {
    String name() default  "";
}
