package com.group9.web.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private String operator; // MTN ou ORANGE
    private String status;   // PENDING, PAID, etc.
}