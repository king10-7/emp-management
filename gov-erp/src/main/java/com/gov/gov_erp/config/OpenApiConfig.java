package com.gov.gov_erp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI govErpOpenAPI() {
        Contact contact = new Contact()
                .name("Government ERP Team")
                .email("support@gov.rw");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Government of Rwanda ERP - Employee & Payroll API")
                .version("1.0.0")
                .description("""
                        Enterprise Resource Planning Backend API for the Government of Rwanda,
                        specializing in Employee Management and Payroll Management System (PMS).
                        Key features include:
                        - Centralized Employee Personal & Employment Details
                        - Deduction & Tax Configuration (Employee Tax, Pension, Medical, Others, Allowances)
                        - Monthly Payroll Generation & Calculation
                        - Payslip Retrieval (by Employee or Period)
                        - Payroll Approval with Database-Level Message Generation
                        """)
                .contact(contact)
                .license(license);

        return new OpenAPI().info(info);
    }
}
