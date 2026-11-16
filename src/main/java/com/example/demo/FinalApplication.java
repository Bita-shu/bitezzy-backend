package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "com.example.Final.controller" , "com.example.Final.service"})
@EntityScan(basePackages = "com.example.Final.user")
@EnableJpaRepositories(basePackages = "com.example.Final.user.repository")
public class FinalApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(FinalApplication.class, args);
	}

}