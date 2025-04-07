package com.example.gym_bro_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GymBroRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymBroRestApiApplication.class, args);
	}

}
