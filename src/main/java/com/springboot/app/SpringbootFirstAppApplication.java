
package com.springboot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.springboot.app.entity")
@EnableJpaRepositories(basePackages = "com.springboot.app.repository")
public class SpringbootFirstAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootFirstAppApplication.class, args);

	}

}