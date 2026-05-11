package com.group9.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDTO {
    @NotNull
    private Long variantId;
    @Min(1)
    private Integer quantity;
}