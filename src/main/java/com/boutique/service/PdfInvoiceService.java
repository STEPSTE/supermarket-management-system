package com.group9.service;

import com.group9.domain.model.Order;
import com.group9.domain.model.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceService {

    /**
     * Génère une facture PDF professionnelle et retourne le chemin du fichier généré.
     * @param order La commande à facturer
     * @return Le chemin relatif du fichier PDF (ex: "invoices/facture_1.pdf")
     */
    public String generateInvoice(Order order) {
        Document document = new Document(PageSize.A4);
        String fileName = "invoices/facture_" + order.getId() + ".pdf";
        
        try {
            // Création du dossier s'il n'existe pas
            Files.createDirectories(Paths.get("invoices"));
            
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            // --- STYLES DES POLICES ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

            // --- EN-TÊTE ---
            Paragraph brand = new Paragraph("GROUP 9 SHOP", titleFont);
            brand.setAlignment(Element.ALIGN_RIGHT);
            document.add(brand);

            Paragraph subTitle = new Paragraph("Facture N° " + order.getId(), FontFactory.getFont(FontFactory.HELVETICA, 14));
            subTitle.setAlignment(Element.ALIGN_RIGHT);
            document.add(subTitle);
            document.add(new Paragraph(" ")); // Espacement

            // --- INFOS CLIENT & COMMANDE ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            infoTable.addCell(getNoBorderCell("Facturé à :\n" + order.getClientFullName() + "\n" + order.getDeliveryAddress(), normalFont));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            infoTable.addCell(getNoBorderCell("Date : " + order.getOrderDate().format(formatter) + 
                                             "\nOpérateur : " + order.getClientOperator() + 
                                             "\nEmail : " + order.getClientEmail(), normalFont));
            
            document.add(infoTable);
            document.add(new Paragraph(" "));

            // --- TABLEAU DES PRODUITS ---
            PdfPTable table = new PdfPTable(4); // 4 colonnes
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1, 2, 2}); // Proportions des colonnes

            // Entête du tableau
            addHeaderCell(table, "Désignation", headerFont);
            addHeaderCell(table, "Qté", headerFont);
            addHeaderCell(table, "Prix Unit. (FCFA)", headerFont);
            addHeaderCell(table, "Total (FCFA)", headerFont);

            // Lignes des produits
            for (OrderItem item : order.getItems()) {
                // On affiche le nom du produit et le SKU de la variante
                String productDetails = item.getVariant().getProduct().getName() + " - " + item.getVariant().getSku();
                table.addCell(new Phrase(productDetails, normalFont));
                
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                
                table.addCell(new Phrase(String.format("%,.0f", item.getPriceAtPurchase()), normalFont));
                
                double rowTotal = item.getQuantity() * item.getPriceAtPurchase().doubleValue();
                table.addCell(new Phrase(String.format("%,.0f", rowTotal), boldFont));
            }

            document.add(table);

            // --- TOTAL FINAL ---
            Paragraph total = new Paragraph("\n\nMONTANT TOTAL À PAYER : " + String.format("%,.0f", order.getTotalAmount()) + " FCFA", titleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            
            // CRITIQUE : On retourne le chemin pour que l'EmailService puisse le trouver
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Échec de la génération de la facture pour la commande " + order.getId(), e);
        }
    }

    // Méthodes utilitaires pour le design
    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(44, 62, 80)); // Bleu nuit professionnel
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private PdfPCell getNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(10);
        return cell;
    }
}