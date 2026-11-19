package com.kafka.consumer.service;



import com.kafka.consumer.annotation.ServiceName;
import com.kafka.consumer.dto.request.Organization;

import com.kafka.consumer.entity.OrganizationTable;
import com.kafka.consumer.mapper.OrganizationMapper;
import com.kafka.consumer.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrganizationConsumerService {

    private static final String ORGANIZATION_CREATION = "Organization creation ";
    private OrganizationRepository organizationRepository;
    private OrganizationMapper organizationMapper;

    @KafkaListener(
            topics = "organization-topic",
            groupId = "organization-consumer-group"
    )
    @ServiceName(name = "organization")
    public void consumeOrganization(Organization organization) {
        // Business logic goes here
        if(Objects.isNull(organization.getOrganizationId())){
                log.error("SKIPPED ORGANIZATION DUE TO ORGANIZATION_ID :{} | IS NULL ",organization.getOrganizationId());
                return;
        }
        processOrganization(organization);
        log.info(
                "{} SUCCESS | Org ID: {} | Name: {}",
                ORGANIZATION_CREATION,
                organization.getOrganizationId(),
                organization.getName()
        );

    }



    @Transactional
    public void processOrganization(Organization organization) {
            if (organizationRepository.existsByOrganizationId(organization.getOrganizationId())) {
                log.warn("ORGANIZATION WITH ORG_ID :{} | ALREADY EXIST ",organization.getOrganizationId());
                return;
            }
            OrganizationTable organizationTable = organizationMapper.toOrgTable(organization);
             organizationRepository.save(organizationTable);
    }

}

