package com.group9.repository;

import com.group9.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.clientOperator = 'MTN'")
    Long countMtnOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.clientOperator = 'ORANGE'")
    Long countOrangeOrders();

    // Requête pour trouver le produit le plus vendu (en quantité)
    @Query(value = "SELECT p.name FROM order_items oi " +
                   "JOIN product_variants pv ON oi.variant_id = pv.id " +
                   "JOIN products p ON pv.product_id = p.id " +
                   "GROUP BY p.id ORDER BY SUM(oi.quantity) DESC LIMIT 1", 
           nativeQuery = true)
    String findTopSellingProductName();
    // À ajouter dans OrderRepository.java

    @Query(value = "SELECT client_full_name FROM orders " +
                "WHERE status = 'PAID' " +
                "GROUP BY client_email " +
                "ORDER BY SUM(total_amount) DESC LIMIT 1", 
        nativeQuery = true)
    String findBestClientName();
}