package com.kafka.producer.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HasRole {
    String[] value();
}
