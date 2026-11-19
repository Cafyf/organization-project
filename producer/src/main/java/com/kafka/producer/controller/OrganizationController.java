package com.kafka.producer.controller;


import com.kafka.producer.annotation.HasRole;
import com.kafka.producer.dtos.request.OrganizationRequestDto;
import com.kafka.producer.dtos.response.OrganizationPublishResponse;
import com.kafka.producer.services.OrganizationProducerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationProducerService organizationProducerService;

    public OrganizationController(
            OrganizationProducerService producerService) {
        this.organizationProducerService = producerService;
    }

    @PostMapping("/publish")
    @HasRole({"admin", "client"})
    public CompletableFuture<ResponseEntity<List<OrganizationPublishResponse>>>
    publishOrganizations(
        @Valid @RequestBody List<OrganizationRequestDto> organizations) {

        return organizationProducerService
                .publishOrganizations(organizations)
                .thenApply(ResponseEntity::ok);
    }
}
