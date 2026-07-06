package com.aditya.f1hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class F1HubApplication {

	public static void main(String[] args) {
		SpringApplication.run(F1HubApplication.class, args);
	}

}