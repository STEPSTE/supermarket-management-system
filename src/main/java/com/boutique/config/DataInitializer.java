package com.boutique.config;

import com.boutique.model.*;
import com.boutique.repository.CategoryRepository;
import com.boutique.repository.ProductRepository;
import com.boutique.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(UserRepository userRepository, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("✅ Base SQLite déjà initialisée");
            return;
        }

        User admin = new User("Admin", "admin@boutique.com", Role.ADMIN);
        User vendor = new User("Alice Vendeuse", "alice@boutique.com", Role.VENDOR);
        User customer = new User("Bob Client", "bob@client.com", Role.CUSTOMER);
        
        userRepository.save(admin);
        userRepository.save(vendor);
        userRepository.save(customer);

        Category electronics = new Category("Électronique");
        Category clothing = new Category("Vêtements");
        categoryRepository.save(electronics);
        categoryRepository.save(clothing);

        Product phone = new Product();
        phone.setName("iPhone 15 Pro");
        phone.setDescription("Smartphone haut de gamme");
        phone.setPrice(999.99);
        phone.setStockQuantity(50);
        phone.setStatus(ProductStatus.DISPONIBLE);
        phone.setCategory(electronics);
        phone.setCreatedBy(vendor);
        phone.setPhotos(List.of(new Photo("https://example.com/iphone.jpg", true)));
        phone.setVariants(List.of(new Variant("Couleur", "Titanium"), new Variant("Stockage", "256GB")));
        
        productRepository.save(phone);

        Product tshirt = new Product();
        tshirt.setName("T-Shirt Premium");
        tshirt.setDescription("Coton bio");
        tshirt.setPrice(29.99);
        tshirt.setStockQuantity(100);
        tshirt.setStatus(ProductStatus.DISPONIBLE);
        tshirt.setCategory(clothing);
        tshirt.setCreatedBy(vendor);
        tshirt.setVariants(List.of(new Variant("Taille", "M"), new Variant("Couleur", "Noir")));
        
        productRepository.save(tshirt);

        System.out.println("✅ Données initiales chargées dans SQLite !");
    }
}