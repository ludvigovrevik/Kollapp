package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"api", "core", "persistence"})
public class KollAppSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KollAppSpringApplication.class, args);
    }
}