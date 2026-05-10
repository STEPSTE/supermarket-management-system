package com.boutique.controller;

import com.boutique.dto.ApiResponse;
import com.boutique.dto.PageResponse;
import com.boutique.dto.request.*;
import com.boutique.model.Product;
import com.boutique.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.createProduct(request), "Produit créé"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Product>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.findAll(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> search(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchByName(name, page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.update(id, request), "Produit mis à jour"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Produit supprimé"));
    }

    @PostMapping("/{id}/stock/increment")
    public ResponseEntity<ApiResponse<Product>> incrementStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.incrementStock(id, request.quantity()), "Stock incrémenté"));
    }

    @PostMapping("/{id}/stock/decrement")
    public ResponseEntity<ApiResponse<Product>> decrementStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.decrementStock(id, request.quantity()), "Stock décrémenté"));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<Product>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDto request) {
        return ResponseEntity.ok(ApiResponse.success(productService.addComment(id, request), "Commentaire ajouté"));
    }
}