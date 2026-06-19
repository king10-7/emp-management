package com.gov.gov_erp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080" + contextPath);
        server.setDescription("Government ERP - Development Server");

        Contact contact = new Contact()
                .name("Government ERP Team")
                .email("support@gov.rw");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Government of Rwanda - Employee & Payroll Management API")
                .version("1.0.0")
                .description("""
                        Backend API for the Government of Rwanda ERP system, 
                        focusing on Employee Management and Payroll Management System (PMS).
                        Features include employee CRUD operations, deduction rate management, 
                        payroll generation, payslip retrieval, and payroll approval with database-level messaging.
                        """)
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
