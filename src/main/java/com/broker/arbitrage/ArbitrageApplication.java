package com.broker.arbitrage;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ArbitrageApplication {

	public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();  // loads .env file
        System.setProperty("rapidapi.key", dotenv.get("RAPIDAPI_KEY"));
        System.out.println("RAPIDAPI_KEY: " + dotenv.get("RAPIDAPI_KEY"));
        SpringApplication.run(ArbitrageApplication.class, args);
	}

}
