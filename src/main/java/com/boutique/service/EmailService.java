package com.group9.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendInvoiceEmail(String toEmail, String customerName, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Votre facture Group 9 Shop - Commande validée");
            helper.setText("Bonjour " + customerName + ",\n\nMerci pour votre achat ! Vous trouverez ci-joint votre facture ultra premium.\n\nL'équipe Group 9 Shop.");

            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment("Facture_Group9.pdf", file);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}