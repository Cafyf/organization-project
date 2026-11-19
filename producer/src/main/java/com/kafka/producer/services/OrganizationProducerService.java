package com.kafka.producer.services;
import com.kafka.producer.dtos.request.OrganizationRequestDto;
import com.kafka.producer.dtos.response.OrganizationPublishResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrganizationProducerService {

    @Value("${kafka.broker.topic.name}")
    private  String topic ;

    private final KafkaTemplate<String, OrganizationRequestDto> kafkaTemplate;

    public OrganizationProducerService(
            KafkaTemplate<String, OrganizationRequestDto> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<List<OrganizationPublishResponse>>
    publishOrganizations(List<OrganizationRequestDto> organizations) {


        List<CompletableFuture<OrganizationPublishResponse>> futures =
                organizations.stream()
                        .map(this::publishOrganization)
                        .toList();

        return CompletableFuture.allOf(
                futures.toArray(new CompletableFuture<?>[0])
        ).thenApply(result ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        );
    }

    private CompletableFuture<OrganizationPublishResponse> publishOrganization(
            OrganizationRequestDto organization) {

        Long orgId = organization.getOrganizationId();

        try {

            return kafkaTemplate.send(
                    topic,
                    String.valueOf(orgId),
                    organization
            ).handle((result, exception) -> {

                if (exception != null) {

                    log.error(
                            "Kafka publication failed | Org ID: {} | Reason: {}",
                            orgId,
                            exception.getMessage(),
                            exception
                    );

                    return OrganizationPublishResponse.builder()
                            .organizationId(orgId)
                            .status("FAILED")
                            .message("Organization publication failed")
                            .errorMessage(exception.getMessage())
                            .topic(topic)
                            .build();
                }

                log.info(
                        "Kafka publication successful | Org ID: {} | Partition: {} | Offset: {}",
                        orgId,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );

                return OrganizationPublishResponse.builder()
                        .organizationId(orgId)
                        .status("SUCCESS")
                        .message("Organization published successfully")
                        .topic(result.getRecordMetadata().topic())
                        .partition(result.getRecordMetadata().partition())
                        .offset(result.getRecordMetadata().offset())
                        .build();
            });

        } catch (Exception exception) {

            log.error(
                    "Unexpected error while publishing organization | Org ID: {}",
                    orgId,
                    exception
            );

            return CompletableFuture.completedFuture(
                    OrganizationPublishResponse.builder()
                            .organizationId(orgId)
                            .status("FAILED")
                            .message("Unexpected publication error")
                            .errorMessage(exception.getMessage())
                            .topic(topic)
                            .build()
            );
        }
    }
}

