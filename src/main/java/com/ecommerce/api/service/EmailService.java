package com.ecommerce.api.service;

import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    // ── Méthode générique d'envoi HTML ────────────────────────

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(message);
            System.out.println("✅ Email envoyé à : " + to + " — Sujet : " + subject);
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email à " + to + " : " + e.getMessage());
            // On ne fait pas planter l'API si l'email échoue
        }
    }

    // ── Méthode générique d'envoi texte simple ─────────────────

    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Erreur email simple : " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  EMAILS UTILISATEURS
    // ══════════════════════════════════════════════════════════

    // Email de bienvenue après inscription
    public void sendWelcomeEmail(User user) {
        String subject = "🎉 Bienvenue sur notre boutique, " + user.getFirstName() + " !";
        String html = buildWelcomeEmailHtml(user);
        sendHtmlEmail(user.getEmail(), subject, html);
    }

    // Email de confirmation de compte activé/désactivé
    public void sendAccountStatusEmail(User user) {
        String status  = user.isActive() ? "activé" : "désactivé";
        String emoji   = user.isActive() ? "✅" : "⛔";
        String subject = emoji + " Votre compte a été " + status;
        String html    = buildAccountStatusHtml(user);
        sendHtmlEmail(user.getEmail(), subject, html);
    }

    // Email de suppression de compte
    public void sendAccountDeletedEmail(String email, String firstName) {
        String subject = "🗑️ Votre compte a été supprimé";
        String text    = "Bonjour " + firstName + ",\n\n"
                       + "Votre compte a été supprimé de notre plateforme.\n"
                       + "Si vous n'êtes pas à l'origine de cette action, contactez-nous immédiatement.\n\n"
                       + "Cordialement,\nL'équipe E-Commerce";
        sendSimpleEmail(email, subject, text);
    }

    // ══════════════════════════════════════════════════════════
    //  EMAILS PRODUITS
    // ══════════════════════════════════════════════════════════

    // Notification rupture de stock (envoyée à l'admin)
    public void sendOutOfStockAlert(Product product, String adminEmail) {
        String subject = "⚠️ Rupture de stock — " + product.getName();
        String html    = buildOutOfStockHtml(product);
        sendHtmlEmail(adminEmail, subject, html);
    }

    // Notification nouveau produit publié
    public void sendProductPublishedEmail(Product product, String adminEmail) {
        String subject = "✅ Produit publié — " + product.getName();
        String text    = "Le produit \"" + product.getName() + "\" (ID: " + product.getId() + ")\n"
                       + "vient d'être publié avec le statut ACTIVE.\n"
                       + "Prix : " + product.getBasePrice() + " €\n"
                       + "Catégorie : " + product.getCategory();
        sendSimpleEmail(adminEmail, subject, text);
    }

    // ══════════════════════════════════════════════════════════
    //  EMAILS COMMANDES
    // ══════════════════════════════════════════════════════════

    // Confirmation de commande au client
    public void sendOrderConfirmationEmail(Order order, User user) {
        String subject = "✅ Commande #" + order.getId() + " confirmée !";
        String html    = buildOrderConfirmationHtml(order, user);
        sendHtmlEmail(user.getEmail(), subject, html);
    }

    // Notification changement de statut commande
    public void sendOrderStatusUpdateEmail(Order order, User user, String oldStatus) {
        String emoji = getStatusEmoji(order.getStatus());
        String subject = emoji + " Commande #" + order.getId() + " — " + order.getStatus();
        String html    = buildOrderStatusHtml(order, user, oldStatus);
        sendHtmlEmail(user.getEmail(), subject, html);
    }

    // ══════════════════════════════════════════════════════════
    //  TEMPLATES HTML DES EMAILS
    // ══════════════════════════════════════════════════════════

    private String buildWelcomeEmailHtml(User user) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='"
            + "font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 20px;'>"
            + "<div style='max-width: 600px; margin: 0 auto; background: white; "
            + "border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>"
            + "<div style='background: linear-gradient(135deg, #667eea, #764ba2); "
            + "padding: 40px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0; font-size: 28px;'>🛒 Bienvenue !</h1>"
            + "</div>"
            + "<div style='padding: 40px;'>"
            + "<h2 style='color: #333;'>Bonjour " + user.getFirstName() + " " + user.getLastName() + " 👋</h2>"
            + "<p style='color: #666; line-height: 1.6;'>Votre compte a été créé avec succès sur notre boutique.</p>"
            + "<div style='background: #f8f9fa; border-radius: 6px; padding: 20px; margin: 20px 0;'>"
            + "<p style='margin: 0; color: #555;'><strong>📧 Email :</strong> " + user.getEmail() + "</p>"
            + "<p style='margin: 8px 0 0; color: #555;'><strong>👤 Rôle :</strong> " + user.getRole() + "</p>"
            + "</div>"
            + "<p style='color: #666;'>Vous pouvez dès maintenant explorer notre catalogue et passer vos premières commandes.</p>"
            + "<div style='text-align: center; margin: 30px 0;'>"
            + "<a href='http://localhost:5173' style='background: #667eea; color: white; "
            + "padding: 14px 32px; border-radius: 6px; text-decoration: none; font-weight: bold;'>"
            + "🛍️ Explorer la boutique</a>"
            + "</div>"
            + "</div>"
            + "<div style='background: #f8f9fa; padding: 20px; text-align: center; color: #aaa; font-size: 12px;'>"
            + "© 2026 E-Commerce API — Cet email a été envoyé automatiquement, merci de ne pas y répondre."
            + "</div></div></body></html>";
    }

    private String buildAccountStatusHtml(User user) {
        String color  = user.isActive() ? "#28a745" : "#dc3545";
        String status = user.isActive() ? "activé ✅" : "désactivé ⛔";
        return "<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;'>"
            + "<div style='max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 40px;'>"
            + "<h2 style='color: " + color + ";'>Statut de votre compte modifié</h2>"
            + "<p>Bonjour <strong>" + user.getFirstName() + "</strong>,</p>"
            + "<p>Votre compte a été <strong style='color: " + color + ";'>" + status + "</strong>.</p>"
            + "<p>Si vous n'êtes pas à l'origine de cette action, contactez notre support.</p>"
            + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
            + "<p style='color: #aaa; font-size: 12px;'>© 2026 E-Commerce API</p>"
            + "</div></body></html>";
    }

    private String buildOutOfStockHtml(Product product) {
        return "<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;'>"
            + "<div style='max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden;'>"
            + "<div style='background: #dc3545; padding: 30px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0;'>⚠️ Rupture de stock</h1>"
            + "</div>"
            + "<div style='padding: 30px;'>"
            + "<h3 style='color: #333;'>" + product.getName() + "</h3>"
            + "<table style='width: 100%; border-collapse: collapse;'>"
            + "<tr style='background: #f8f9fa;'><td style='padding: 10px; font-weight: bold;'>ID Produit</td>"
            + "<td style='padding: 10px;'>#" + product.getId() + "</td></tr>"
            + "<tr><td style='padding: 10px; font-weight: bold;'>Catégorie</td>"
            + "<td style='padding: 10px;'>" + product.getCategory() + "</td></tr>"
            + "<tr style='background: #f8f9fa;'><td style='padding: 10px; font-weight: bold;'>Marque</td>"
            + "<td style='padding: 10px;'>" + product.getBrand() + "</td></tr>"
            + "<tr><td style='padding: 10px; font-weight: bold;'>Statut actuel</td>"
            + "<td style='padding: 10px; color: #dc3545; font-weight: bold;'>OUT_OF_STOCK</td></tr>"
            + "</table>"
            + "<p style='color: #666; margin-top: 20px;'>Veuillez réapprovisionner ce produit dès que possible.</p>"
            + "</div></div></body></html>";
    }

    private String buildOrderConfirmationHtml(Order order, User user) {
        StringBuilder itemsHtml = new StringBuilder();
        if (order.getItems() != null) {
            for (Order.OrderItem item : order.getItems()) {
                itemsHtml.append("<tr>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee;'>").append(item.getProductName()).append("</td>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; text-align: center;'>").append(item.getQuantity()).append("</td>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; text-align: right;'>").append(String.format("%.2f €", item.getSubtotal())).append("</td>")
                    .append("</tr>");
            }
        }
        return "<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;'>"
            + "<div style='max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden;'>"
            + "<div style='background: #28a745; padding: 30px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0;'>✅ Commande confirmée !</h1>"
            + "</div>"
            + "<div style='padding: 30px;'>"
            + "<p>Bonjour <strong>" + user.getFirstName() + "</strong>,</p>"
            + "<p>Votre commande <strong>#" + order.getId() + "</strong> a bien été reçue et est en cours de traitement.</p>"
            + "<h3 style='color: #333; border-bottom: 2px solid #28a745; padding-bottom: 8px;'>Détail de votre commande</h3>"
            + "<table style='width: 100%; border-collapse: collapse;'>"
            + "<thead><tr style='background: #f8f9fa;'>"
            + "<th style='padding: 10px; text-align: left;'>Produit</th>"
            + "<th style='padding: 10px; text-align: center;'>Qté</th>"
            + "<th style='padding: 10px; text-align: right;'>Prix</th>"
            + "</tr></thead>"
            + "<tbody>" + itemsHtml + "</tbody>"
            + "</table>"
            + "<div style='text-align: right; margin-top: 16px; font-size: 18px;'>"
            + "<strong>Total : " + String.format("%.2f €", order.getTotal()) + "</strong>"
            + "</div>"
            + "<div style='background: #f8f9fa; border-radius: 6px; padding: 16px; margin-top: 20px;'>"
            + "<p style='margin: 0; color: #555;'><strong>📦 Livraison à :</strong><br>" + order.getShippingAddress() + "</p>"
            + "</div>"
            + "</div>"
            + "<div style='background: #f8f9fa; padding: 20px; text-align: center; color: #aaa; font-size: 12px;'>"
            + "© 2026 E-Commerce API</div></div></body></html>";
    }

    private String buildOrderStatusHtml(Order order, User user, String oldStatus) {
        String color = getStatusColor(order.getStatus());
        return "<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;'>"
            + "<div style='max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden;'>"
            + "<div style='background: " + color + "; padding: 30px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0;'>" + getStatusEmoji(order.getStatus()) + " Mise à jour de commande</h1>"
            + "</div>"
            + "<div style='padding: 30px;'>"
            + "<p>Bonjour <strong>" + user.getFirstName() + "</strong>,</p>"
            + "<p>Le statut de votre commande <strong>#" + order.getId() + "</strong> a changé :</p>"
            + "<div style='display: flex; align-items: center; gap: 16px; margin: 20px 0;'>"
            + "<span style='background: #e9ecef; padding: 8px 16px; border-radius: 20px; color: #666;'>" + oldStatus + "</span>"
            + "<span style='font-size: 20px;'>→</span>"
            + "<span style='background: " + color + "; padding: 8px 16px; border-radius: 20px; color: white; font-weight: bold;'>"
            + order.getStatus() + "</span>"
            + "</div>"
            + "<p style='color: #666;'>" + getStatusDescription(order.getStatus()) + "</p>"
            + "</div>"
            + "<div style='background: #f8f9fa; padding: 20px; text-align: center; color: #aaa; font-size: 12px;'>"
            + "© 2026 E-Commerce API</div></div></body></html>";
    }

    // ── Helpers ───────────────────────────────────────────────

    private String getStatusEmoji(String status) {
        return switch (status) {
            case "PENDING"   -> "⏳";
            case "CONFIRMED" -> "✅";
            case "SHIPPED"   -> "🚚";
            case "DELIVERED" -> "📦";
            case "CANCELLED" -> "❌";
            default          -> "📋";
        };
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "CONFIRMED" -> "#28a745";
            case "SHIPPED"   -> "#007bff";
            case "DELIVERED" -> "#6f42c1";
            case "CANCELLED" -> "#dc3545";
            default          -> "#6c757d";
        };
    }

    private String getStatusDescription(String status) {
        return switch (status) {
            case "CONFIRMED" -> "Votre commande a été confirmée et est en cours de préparation.";
            case "SHIPPED"   -> "Votre commande est en route ! Vous la recevrez dans 2-5 jours ouvrés.";
            case "DELIVERED" -> "Votre commande a été livrée. Nous espérons que vous êtes satisfait(e) !";
            case "CANCELLED" -> "Votre commande a été annulée. Contactez-nous pour plus d'informations.";
            default          -> "Le statut de votre commande a été mis à jour.";
        };
    }
}