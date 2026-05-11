package com.boutique.boutique_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.boutique")
@EnableJpaRepositories(basePackages = "com.boutique.repository")
@EntityScan(basePackages = "com.boutique.model")
public class BoutiqueApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoutiqueApiApplication.class, args);
    }
}