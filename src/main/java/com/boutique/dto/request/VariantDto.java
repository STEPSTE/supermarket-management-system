package com.boutique.dto.request;

public record VariantDto(String type, String value, Integer stock, Double price, Boolean available) {}