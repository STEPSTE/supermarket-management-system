package com.group9.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    @NotBlank @Email
    private String clientEmail;

    private String clientPhoneNumber; // Optionnel : récupéré si vide

    @NotBlank
    private String deliveryAddress;

    @NotEmpty(message = "La commande doit contenir au moins un article")
    private List<OrderItemRequestDTO> items;
}