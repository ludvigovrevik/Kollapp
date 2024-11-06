package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"api.controller", "api.service", "core", "persistence"})
public class KollAppSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(KollAppSpringApplication.class, args);
    }
}