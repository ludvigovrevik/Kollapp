package com.gr2409.kollapp.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.gr2409", "core", "persistence"})
public class KollAppSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KollAppSpringApplication.class, args);
    }
}