package com.boutique.boutique_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.boutique")  // ← AJOUTE CETTE LIGNE
public class BoutiqueApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoutiqueApiApplication.class, args);
    }
}