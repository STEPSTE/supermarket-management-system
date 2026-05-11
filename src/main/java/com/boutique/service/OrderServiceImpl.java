package com.group9.service;

import com.group9.domain.enums.OrderStatus;
import com.group9.domain.model.*;
import com.group9.exception.ResourceNotFoundException;
import com.group9.repository.*;
import com.group9.util.PhoneUtils;
import com.group9.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;
    private final PhoneUtils phoneUtils;
    private final PdfInvoiceService pdfInvoiceService;
    private final EmailService emailService; // Injecté proprement en haut

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setClientEmail(dto.getClientEmail());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setStatus(OrderStatus.PENDING);

        String finalPhone = dto.getClientPhoneNumber();
        String finalName = "Client Invité";

        if (finalPhone == null || finalPhone.isBlank()) {
            User existingUser = userRepository.findByEmail(dto.getClientEmail())
                    .orElseThrow(() -> new RuntimeException("Numéro de téléphone requis pour un nouveau client."));
            finalPhone = existingUser.getPhoneNumber();
            finalName = existingUser.getFullName();
        }
        
        order.setClientPhoneNumber(finalPhone);
        order.setClientFullName(finalName);
        order.setClientOperator(phoneUtils.detectOperator(finalPhone));

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            ProductVariant variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

            if (variant.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour le produit : " + variant.getSku());
            }

            variant.setStockQuantity(variant.getStockQuantity() - itemDto.getQuantity());
            variantRepository.save(variant);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .variant(variant)
                    .quantity(itemDto.getQuantity())
                    .priceAtPurchase(variant.getPrice())
                    .build();
            
            order.getItems().add(item);
            total = total.add(variant.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        order.setTotalAmount(total);
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDTO updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        // 1. Remise en stock si ANNULÉE (uniquement si elle n'était pas déjà annulée)
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                ProductVariant variant = item.getVariant();
                variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                variantRepository.save(variant);
            }
        }

        // 2. Mise à jour et sauvegarde du statut
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        // 3. Si PAYÉE : Génération facture -> Envoi Mail
        if (newStatus == OrderStatus.PAID) {
            // On récupère le chemin du fichier généré
            String filePath = pdfInvoiceService.generateInvoice(savedOrder); 
            
            // On envoie le mail avec la pièce jointe
            emailService.sendInvoiceEmail(
                savedOrder.getClientEmail(), 
                savedOrder.getClientFullName(), 
                filePath
            );
        }

        return mapToResponse(savedOrder);
    }

    private OrderResponseDTO mapToResponse(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .operator(order.getClientOperator())
                .status(order.getStatus().name())
                .build();
    }
}