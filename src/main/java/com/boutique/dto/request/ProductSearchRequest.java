package com.boutique.dto.request;

import com.boutique.model.ProductStatus;

public record ProductSearchRequest(
    String name,
    Long categoryId,
    Double minPrice,
    Double maxPrice,
    ProductStatus status
) {}