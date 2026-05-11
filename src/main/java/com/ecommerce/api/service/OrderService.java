package com.ecommerce.api.service;

import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.User;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository  userRepository;
    @Autowired private EmailService    emailService;

    public List<Order>     getAllOrders()               { return orderRepository.findAll(); }
    public Optional<Order> getOrderById(Long id)        { return orderRepository.findById(id); }
    public List<Order>     getOrdersByUser(Long userId) { return orderRepository.findByUserId(userId); }
    public List<Order>     getOrdersByStatus(String s)  { return orderRepository.findByStatus(s); }

    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        // Calculer le total
        if (order.getItems() != null) {
            double total = order.getItems().stream()
                .mapToDouble(item -> { item.setSubtotal(item.getUnitPrice() * item.getQuantity()); return item.getSubtotal(); })
                .sum();
            order.setTotal(total);
        }
        Order saved = orderRepository.save(order);

        // ✅ EMAIL — Confirmation de commande
        userRepository.findById(order.getUserId()).ifPresent(user ->
            emailService.sendOrderConfirmationEmail(saved, user)
        );

        return saved;
    }

    public Order updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Commande #" + id + " introuvable"));

        String oldStatus = order.getStatus();
        order.setStatus(newStatus.toUpperCase());
        Order saved = orderRepository.save(order);

        // ✅ EMAIL — Notification changement de statut
        userRepository.findById(order.getUserId()).ifPresent(user ->
            emailService.sendOrderStatusUpdateEmail(saved, user, oldStatus)
        );

        return saved;
    }

    public boolean deleteOrder(Long id) {
        if (orderRepository.findById(id).isEmpty())
            throw new NoSuchElementException("Commande introuvable");
        orderRepository.deleteById(id);
        return true;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalOrders",     orderRepository.count());
        stats.put("pendingOrders",   orderRepository.countByStatus("PENDING"));
        stats.put("confirmedOrders", orderRepository.countByStatus("CONFIRMED"));
        stats.put("shippedOrders",   orderRepository.countByStatus("SHIPPED"));
        stats.put("deliveredOrders", orderRepository.countByStatus("DELIVERED"));
        stats.put("cancelledOrders", orderRepository.countByStatus("CANCELLED"));
        return stats;
    }
}