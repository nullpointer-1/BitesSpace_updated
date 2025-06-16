package com.example.shopmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication

@EnableMongoRepositories(basePackages = "com.example.shopmanagement.repository")
public class ShopManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopManagementApplication.class, args);
	}

}