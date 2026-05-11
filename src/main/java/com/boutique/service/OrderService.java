package com.group9.service;

import com.group9.domain.enums.OrderStatus; // Importé
import com.group9.web.dto.OrderRequestDTO;
import com.group9.web.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO dto);
    
    // AJOUTE CETTE LIGNE :
    OrderResponseDTO updateStatus(Long id, OrderStatus newStatus);
}