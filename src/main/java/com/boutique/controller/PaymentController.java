package com.group9.web.controller;

import com.group9.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<String> pay(
            @RequestParam Long orderId, 
            @RequestParam BigDecimal amount,
            @RequestParam String reference,
            @RequestParam String adminNumber,
            @RequestParam String adminName) {
            
        String message = paymentService.processPayment(orderId, amount, reference, adminNumber, adminName);
        return ResponseEntity.ok(message);
    }
}