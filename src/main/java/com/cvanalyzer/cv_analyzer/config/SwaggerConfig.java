package com.cvanalyzer.cv_analyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cvAnalyzerOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CV Analyzer API")
                        .description("API for CV analysis and enhancement")
                        .version("v1.0"));
    }
}
