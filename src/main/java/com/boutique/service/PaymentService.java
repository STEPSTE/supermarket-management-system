package com.group9.service;

import com.group9.domain.enums.OrderStatus;
import com.group9.domain.model.Order;
import com.group9.domain.model.Transaction;
import com.group9.repository.OrderRepository;
import com.group9.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final PdfInvoiceService pdfInvoiceService;
    private final EmailService emailService;

    @Transactional
    public String processPayment(Long orderId, BigDecimal amount, String ref, String adminNum, String adminName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // 1. Enregistrement de la transaction pour la traçabilité
        Transaction tx = Transaction.builder()
                .amountPaid(amount)
                .transactionRef(ref)
                .adminServiceNumber(adminNum)
                .receiverName(adminName)
                .paymentDate(LocalDateTime.now())
                .order(order)
                .build();
        transactionRepository.save(tx);

        // 2. Calcul du cumul des paiements reçus pour cette commande
        List<Transaction> transactions = transactionRepository.findByOrderId(orderId);
        BigDecimal alreadyPaid = transactions.stream()
                .map(Transaction::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Vérification du solde
        if (alreadyPaid.compareTo(order.getTotalAmount()) >= 0) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            
            // Déclenchement facture et mail (car paiement 100% complété)
            String path = pdfInvoiceService.generateInvoice(order);
            emailService.sendInvoiceEmail(order.getClientEmail(), order.getClientFullName(), path);
            
            return "Succès : Paiement complet reçu. La commande est maintenant validée.";
        } else {
            BigDecimal reste = order.getTotalAmount().subtract(alreadyPaid);
            return "Info : Paiement partiel reçu de " + amount + " FCFA. Il reste encore " + reste + " FCFA à verser.";
        }
    }
}