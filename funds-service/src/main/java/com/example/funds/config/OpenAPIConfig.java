package com.example.funds.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI fundsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Funds Management API")
                        .description("API for managing funds transfers, credits, and debits")
                        .version("1.0"));
    }
}