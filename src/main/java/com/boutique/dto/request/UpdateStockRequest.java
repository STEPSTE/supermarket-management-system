package com.boutique.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateStockRequest(@NotNull @Positive Integer quantity) {}