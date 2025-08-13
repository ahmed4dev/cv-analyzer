package com.cvanalyzer.cv_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class CvAnalyzerApplication {
	public static void main(String[] args) {
		SpringApplication.run(CvAnalyzerApplication.class, args);
	}
}
