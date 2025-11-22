package com.upidashboard.upi_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI upiBackendOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UPI Dashboard Backend API")
                        .description("API documentation for the UPI Dashboard Backend application. " +
                                "This backend handles user authentication, payments, transactions, and admin operations.")
                        .version("1.0.0"));
    }
}
