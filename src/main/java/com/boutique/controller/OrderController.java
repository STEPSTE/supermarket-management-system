package com.group9.web.controller;

import com.group9.domain.enums.OrderStatus;
import com.group9.service.OrderService;
import com.group9.web.dto.OrderRequestDTO;
import com.group9.web.dto.OrderResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        return new ResponseEntity<>(orderService.createOrder(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id, 
            @RequestParam OrderStatus newStatus) {
        return ResponseEntity.ok(orderService.updateStatus(id, newStatus));
    }
}