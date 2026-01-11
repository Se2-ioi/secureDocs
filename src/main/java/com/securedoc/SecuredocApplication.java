package com.securedoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SecuredocApplication {

	public static void main(String[] args) {

        SpringApplication.run(SecuredocApplication.class, args);

    }
}
