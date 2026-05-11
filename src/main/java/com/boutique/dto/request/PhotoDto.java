package com.boutique.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PhotoDto(@NotBlank String url) {}