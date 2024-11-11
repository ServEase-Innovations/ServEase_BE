package com.cus.customertab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CustomertabApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomertabApplication.class, args);
	}
}