package com.boutique.dto.request;

import com.boutique.model.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record CreateProductRequest(
    @NotBlank String name,
    String description,
    @NotNull @Positive Double price,
    @NotNull @PositiveOrZero Integer stockQuantity,
    ProductStatus status,
    @NotNull Long createdById,
    Long categoryId,
    List<PhotoDto> photos,
    List<VariantDto> variants
) {}