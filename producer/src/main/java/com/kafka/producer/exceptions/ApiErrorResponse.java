package com.kafka.producer.exceptions;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private String errorCode;

    private int status;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private Map<String, String> validationErrors;
}

