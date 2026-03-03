package com.example.FullstackFhirService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FullstackFhirServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullstackFhirServiceApplication.class, args);
	}
}
