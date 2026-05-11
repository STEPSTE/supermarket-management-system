package com.boutique.controller;

import com.boutique.dto.ApiResponse;
import com.boutique.dto.request.CreateOrderRequest;
import com.boutique.dto.request.UpdateOrderStatusRequest;
import com.boutique.model.Order;
import com.boutique.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(request), "Commande créée et confirmée"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(orderService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.findById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateStatus(id, request), "Statut commande mis à jour"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.findByUserId(userId)));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<ApiResponse<List<Order>>> findHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.findByUserId(userId)));
    }
}