package com.group9.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amountPaid;      // Montant versé
    private LocalDateTime paymentDate;  // Date précise
    private String transactionRef;      // ID de transaction (ex: ID MoMo/OM)
    private String adminServiceNumber;  // Le numéro qui a reçu l'argent
    private String receiverName;        // Nom de l'admin/caissier qui a validé

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}