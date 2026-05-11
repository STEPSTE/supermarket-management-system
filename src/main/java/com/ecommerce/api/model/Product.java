package com.ecommerce.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String category;
    private String brand;
    private double basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    private int     totalStock;
    private double  rating;
    private int     reviewCount;
    private String  createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Variantes — relation One-to-Many
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private List<ProductVariant> variants = new ArrayList<>();

    // Images — relation One-to-Many
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private List<ProductImage> images = new ArrayList<>();

    // Tags — stockés dans une table séparée
    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires (inchangées)
    public String getPrimaryImageUrl() {
        return images.stream()
            .filter(ProductImage::isPrimary)
            .map(ProductImage::getUrl)
            .findFirst()
            .orElse(images.isEmpty() ? null : images.get(0).getUrl());
    }

    public int calculateTotalStock() {
        if (variants.isEmpty()) return totalStock;
        return variants.stream().mapToInt(ProductVariant::getStock).sum();
    }

    public double getMinPrice() {
        if (variants.isEmpty()) return basePrice;
        return variants.stream().mapToDouble(ProductVariant::getPrice).min().orElse(basePrice);
    }
}