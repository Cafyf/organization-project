package com.kafka.producer.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class OrganizationRequestDto
{
    @NotNull
    @JsonProperty("organizationId")
    private Long organizationId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private String address;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("country")
    private String country;
    @JsonIgnore
    private String createDate;

    public OrganizationRequestDto() {
    }

    public OrganizationRequestDto(Long id, String name,
                        String address, String city,
                        String state, String country) {
        this.organizationId = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

}
