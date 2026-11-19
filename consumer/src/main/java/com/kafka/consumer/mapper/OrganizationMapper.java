package com.kafka.consumer.mapper;


import com.kafka.consumer.dto.request.Organization;
import com.kafka.consumer.entity.OrganizationTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    @Mapping(
            target = "createDate",
            source = "createDate",
            qualifiedByName = "stringToLocalDateTime"
    )
    OrganizationTable toOrgTable(Organization org);
    @Mapping(
            target = "createDate",
            source = "createDate",
            qualifiedByName = "localDateTimeToString"
    )
    Organization toOrgDto(OrganizationTable table);

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String date) {

        if (date == null || date.isBlank()) {
            return LocalDateTime.now();
        }

        return LocalDateTime.parse(date, FORMATTER);
    }
    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime date) {

        if (date == null) {
            return "";
        }

        return date.format(FORMATTER);
    }

}
