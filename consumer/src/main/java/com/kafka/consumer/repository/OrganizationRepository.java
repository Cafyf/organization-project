package com.kafka.consumer.repository;

import com.kafka.consumer.entity.OrganizationTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository  extends JpaRepository<OrganizationTable,String> {
    Boolean existsByOrganizationId(Long organizationId);
}
