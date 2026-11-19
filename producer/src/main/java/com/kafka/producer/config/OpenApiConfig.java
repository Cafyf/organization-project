package com.kafka.producer.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "JwtAuth")
        }
)
@SecurityScheme(
        name = "JwtAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "Jwt-token",
        description = "Enter your JWT token"
)
public class OpenApiConfig {
}
