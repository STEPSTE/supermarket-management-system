package com.boutique.dto.request;

import com.boutique.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull OrderStatus status,
    String trackingNumber,
    String shippingAddress
) {}