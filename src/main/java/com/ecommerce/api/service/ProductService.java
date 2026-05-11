package com.ecommerce.api.service;

import com.ecommerce.api.model.*;
import com.ecommerce.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired 
    private EmailService emailService; // Injection du service d'email

    // Récupère l'email admin depuis application.properties (avec valeur par défaut)
    @Value("${app.mail.admin:admin@ecommerce.com}")
    private String adminEmail;

    // ── Lecture ───────────────────────────────────────────────

    public List<Product> getAllProducts() { 
        return repository.findAll(); 
    }

    public Optional<Product> getProductById(Long id) { 
        return repository.findById(id); 
    }

    public List<Product> getByStatus(ProductStatus status) { 
        return repository.findByStatus(status); 
    }

    public List<Product> getByCategory(String category) { 
        return repository.findByCategoryIgnoreCase(category); 
    }

    public List<Product> search(String keyword) { 
        return repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword); 
    }

    public List<Product> getByPriceRange(double min, double max) { 
        return repository.findByBasePriceBetween(min, max); 
    }

    public List<String> getAllCategories() { 
        return repository.findAllCategories(); 
    }

    // ── Création ──────────────────────────────────────────────

    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        // Initialisation des listes pour éviter les NullPointerException avec JPA
        if (product.getVariants() == null) product.setVariants(new ArrayList<>());
        if (product.getImages() == null) product.setImages(new ArrayList<>());
        
        return repository.save(product);
    }

    // ── Mise à jour ───────────────────────────────────────────

    public Product updateProduct(Long id, Product updated) {
        Product existing = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Produit #" + id + " introuvable"));
        
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setBrand(updated.getBrand());
        existing.setBasePrice(updated.getBasePrice());
        existing.setTags(updated.getTags());
        
        return repository.save(existing);
    }

    public Product updateStatus(Long id, ProductStatus newStatus) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Produit #" + id + " introuvable"));

        ProductStatus oldStatus = product.getStatus();
        product.setStatus(newStatus);
        Product saved = repository.save(product);

        // ✅ EMAIL — Notification si un brouillon est publié
        if (newStatus == ProductStatus.ACTIVE && oldStatus == ProductStatus.DRAFT) {
            emailService.sendProductPublishedEmail(saved, adminEmail);
        }

        return saved;
    }

    // ── Suppression ───────────────────────────────────────────

    public boolean deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Produit #" + id + " introuvable");
        }
        repository.deleteById(id);
        return true;
    }

    // ── Variantes et Stock ────────────────────────────────────

    public Product addVariant(Long productId, ProductVariant variant) {
        Product product = repository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException("Produit introuvable"));
        
        product.getVariants().add(variant);
        return repository.save(product);
    }

    public Product updateVariantStock(Long productId, String variantId, int newStock) {
        Product product = repository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException("Produit introuvable"));

        // Mise à jour de la variante spécifique
        product.getVariants().stream()
            .filter(v -> v.getId().equals(variantId))
            .findFirst()
            .ifPresent(v -> {
                v.setStock(newStock);
                v.setAvailable(newStock > 0);
            });

        // Recalcul du stock total et gestion des alertes
        int totalStock = product.calculateTotalStock();
        if (totalStock == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);

            // ✅ EMAIL — Alerte rupture de stock envoyée à l'admin
            emailService.sendOutOfStockAlert(product, adminEmail);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            // Si on rajoute du stock, on peut repasser en ACTIVE
            product.setStatus(ProductStatus.ACTIVE);
        }

        return repository.save(product);
    }

    // ── Images ────────────────────────────────────────────────

    public Product addImage(Long productId, ProductImage image) {
        Product product = repository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException("Produit introuvable"));
        
        if (product.getImages().isEmpty()) image.setPrimary(true);
        product.getImages().add(image);
        return repository.save(product);
    }

    // ── Statistiques ──────────────────────────────────────────

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalProducts",    repository.count());
        stats.put("activeProducts",   repository.findByStatus(ProductStatus.ACTIVE).size());
        stats.put("outOfStock",       repository.findByStatus(ProductStatus.OUT_OF_STOCK).size());
        stats.put("totalCategories",  repository.findAllCategories().size());
        return stats;
    }
}