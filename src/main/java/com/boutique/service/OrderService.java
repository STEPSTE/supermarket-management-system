package com.boutique.service;

import com.boutique.dto.request.CreateOrderRequest;
import com.boutique.dto.request.OrderItemRequest;
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

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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

            // Débiter le stock
            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            if (product.getStockQuantity() == 0) {
                product.setStatus(ProductStatus.RUPTURE);
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
        order.setStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée: " + id));
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}