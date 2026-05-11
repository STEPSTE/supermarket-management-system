package com.group9.repository;

import com.group9.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Cette méthode permet de récupérer toutes les transactions liées à une commande précise
    List<Transaction> findByOrderId(Long orderId);
}