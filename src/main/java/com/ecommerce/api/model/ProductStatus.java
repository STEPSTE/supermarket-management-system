package com.ecommerce.api.model;

public enum ProductStatus {
    ACTIVE,       // Disponible à la vente
    INACTIVE,     // Désactivé temporairement
    OUT_OF_STOCK, // Rupture de stock
    DISCONTINUED, // Arrêté définitivement
    DRAFT         // Brouillon — pas encore publié
}