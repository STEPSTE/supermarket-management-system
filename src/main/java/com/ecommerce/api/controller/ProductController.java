package com.ecommerce.api.controller;

import com.ecommerce.api.model.*;
import com.ecommerce.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService service;

    // ── GET — Lecture ──────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return service.getProductById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Produit #" + id + " introuvable")));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Product>> getByStatus(@PathVariable ProductStatus status) {
        return ResponseEntity.ok(service.getByStatus(status));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(service.getAllCategories());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        return ResponseEntity.ok(service.getByPriceRange(min, max));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    // ── POST — Création ────────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product created = service.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT/PATCH — Mise à jour ────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            return ResponseEntity.ok(service.updateProduct(id, product));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            ProductStatus status = ProductStatus.valueOf(body.get("status").toUpperCase());
            return ResponseEntity.ok(service.updateStatus(id, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Statut invalide."));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE — Suppression ───────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            service.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Produit #" + id + " supprimé avec succès"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Variantes ─────────────────────────────────────────────

    @PostMapping("/{id}/variants")
    public ResponseEntity<?> addVariant(@PathVariable Long id, @RequestBody ProductVariant variant) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addVariant(id, variant));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/variants/{variantId}/stock")
    public ResponseEntity<?> updateVariantStock(
            @PathVariable Long id,
            @PathVariable String variantId,
            @RequestBody Map<String, Integer> body) {
        try {
            return ResponseEntity.ok(service.updateVariantStock(id, variantId, body.get("stock")));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Images ────────────────────────────────────────────────

    @PostMapping("/{id}/images")
    public ResponseEntity<?> addImage(@PathVariable Long id, @RequestBody ProductImage image) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.addImage(id, image));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}