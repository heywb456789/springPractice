package com.tomato.naraclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NaraclubApplication {

	public static void main(String[] args) {
		SpringApplication.run(NaraclubApplication.class, args);
	}

}
