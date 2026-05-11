package com.boutique.dto.request;

import com.boutique.model.ProductStatus;

import java.util.List;

public record UpdateProductRequest(
    String name,
    String description,
    Double price,
    Integer stockQuantity,
    ProductStatus status,
    Long categoryId,
    List<PhotoDto> photos,
    List<VariantDto> variants
) {}