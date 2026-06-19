package com.gov.gov_erp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger/OpenAPI documentation metadata.
 * This information is displayed at the top of the Swagger UI page.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI govErpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Government of Rwanda - ERP System API")
                        .description("Backend REST API for Employee Management and Payroll Management System (PMS). "
                                + "Part of the Enterprise Resource Planning platform for the Government of Rwanda.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RCA Developer")
                                .email("developer@gov.rw")));
    }
}
