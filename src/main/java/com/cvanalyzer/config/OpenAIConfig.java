package com.cvanalyzer.config;


import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("ahmed@cvanalyzer.com");
        contact.setName("CV Analyzer Team");
        contact.setUrl("https://www.cvanalyzer.com");

        Info info = new Info()
                .title("CV Analyzer API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for CV analysis and processing.");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}