package com.example.FullstackMessageService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FullstackMessageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullstackMessageServiceApplication.class, args);
	}
}
