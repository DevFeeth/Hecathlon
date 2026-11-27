package com.spring.henallux.hecathlon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class HecathlonApplication {
	public static void main(String[] args) {
		SpringApplication.run(HecathlonApplication.class, args);
	}

}
