package com.example.universe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI universeOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Universe API")
                .description("Spring Boot REST & GraphQL API for Planets and Moons")
                .version("1.0"));
    }
}
