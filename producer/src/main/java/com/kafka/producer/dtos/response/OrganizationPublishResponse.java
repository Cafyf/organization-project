package com.kafka.producer.dtos.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrganizationPublishResponse {
    private Long organizationId;

    private String status;

    private String message;

    private String topic;

    private Integer partition;

    private Long offset;

    private String errorMessage;
}
