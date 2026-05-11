package com.boutique.service;

import com.boutique.model.Order;
import com.boutique.model.Product;
import com.boutique.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(User user) {
        String subject = "Bienvenue sur Boutique !";
        String text = "Bonjour " + user.getName() + ",\n\nBienvenue ! Votre compte est créé avec succès.";
        sendEmail(user.getEmail(), subject, text);
        System.out.println("📧 [EMAIL SIMULÉ] Bienvenue envoyé à " + user.getEmail());
    }

    public void sendOrderConfirmation(Order order) {
        String subject = "Confirmation de commande #" + order.getId();
        String text = "Votre commande de " + order.getTotalAmount() + "€ est confirmée.\nStatut : " + order.getStatus();
        sendEmail(order.getUser().getEmail(), subject, text);
        System.out.println("📧 [EMAIL SIMULÉ] Confirmation commande à " + order.getUser().getEmail());
    }

    public void sendStockAlert(Product product) {
        String subject = "ALERTE : Rupture de stock";
        String text = "Le produit '" + product.getName() + "' est en rupture de stock.";
        sendEmail("admin@boutique.com", subject, text);
        System.out.println("📧 [EMAIL SIMULÉ] Alerte stock admin pour " + product.getName());
    }

    public void sendShippingNotification(Order order) {
        String subject = "Votre commande est expédiée";
        String text = "Commande #" + order.getId() + " expédiée.\nN° suivi : " + order.getTrackingNumber();
        sendEmail(order.getUser().getEmail(), subject, text);
        System.out.println("📧 [EMAIL SIMULÉ] Expédition à " + order.getUser().getEmail());
    }

    public void sendAccountStatusChange(User user) {
        String status = user.getActive() ? "activé" : "désactivé";
        String subject = "Compte " + status;
        String text = "Votre compte a été " + status + ".";
        sendEmail(user.getEmail(), subject, text);
        System.out.println("📧 [EMAIL SIMULÉ] Statut compte à " + user.getEmail());
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("⚠️ Email non envoyé (SMTP non configuré) : " + e.getMessage());
        }
    }
}