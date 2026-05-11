package com.boutique.service;

import com.boutique.model.Product;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FactureService {

    public String genererFacture(Product product, int quantite) {
        // Créer le dossier factures s'il n'existe pas
        String directoryName = "factures";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String fileName = directoryName + "/facture_" + product.getName().replace(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Style
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            // Contenu
            document.add(new Paragraph("FACTURE DE VENTE", titleFont));
            document.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("Produit : " + product.getName(), normalFont));
            document.add(new Paragraph("Prix unitaire : " + product.getPrice() + " €", normalFont));
            document.add(new Paragraph("Quantité : " + quantite, normalFont));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("TOTAL A PAYER : " + (product.getPrice() * quantite) + " €", titleFont));

            document.close();
            return "Facture générée avec succès : " + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la génération de la facture : " + e.getMessage();
        }
    }
}
