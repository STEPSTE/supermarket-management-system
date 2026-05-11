package com.ecommerce.api.repository;

import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring génère le SQL automatiquement à partir du nom de la méthode
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByBrandIgnoreCase(String brand);
    List<Product> findByBasePriceBetween(double min, double max);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name, String description
    );

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();

    long countByStatus(ProductStatus status);
}