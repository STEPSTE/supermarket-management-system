package com.boutique.service;

import com.boutique.dto.request.CreateOrderRequest;
import com.boutique.dto.request.OrderItemRequest;
import com.boutique.dto.request.UpdateOrderStatusRequest;
import com.boutique.exception.BusinessException;
import com.boutique.exception.ResourceNotFoundException;
import com.boutique.model.*;
import com.boutique.repository.OrderRepository;
import com.boutique.repository.ProductRepository;
import com.boutique.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
                        ProductRepository productRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + request.userId()));

        Order order = new Order();
        order.setUser(user);

        double total = 0.0;

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé: " + itemReq.productId()));

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new BusinessException(
                    String.format("Stock insuffisant pour '%s'. Demandé: %d, Disponible: %d",
                        product.getName(), itemReq.quantity(), product.getStockQuantity())
                );
            }

            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            if (product.getStockQuantity() == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
                emailService.sendStockAlert(product);
            }
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            total += product.getPrice() * itemReq.quantity();
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);
        Order saved = orderRepository.save(order);
        emailService.sendOrderConfirmation(saved);
        return saved;
    }

    public Order updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = findById(id);
        order.setStatus(request.status());
        if (request.trackingNumber() != null) order.setTrackingNumber(request.trackingNumber());
        if (request.shippingAddress() != null) order.setShippingAddress(request.shippingAddress());
        
        Order saved = orderRepository.save(order);
        
        if (request.status() == OrderStatus.SHIPPED) {
            emailService.sendShippingNotification(saved);
        }
        return saved;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée: " + id));
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}