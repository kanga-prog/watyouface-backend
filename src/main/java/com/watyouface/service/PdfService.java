package com.watyouface.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public ByteArrayInputStream generateContractPdf(Contract contract, User user) {
        Document document = new Document(PageSize.A4, 50, 50, 70, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // === 🧩 En-tête du contrat ===
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(33, 37, 41));
            Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC, BaseColor.GRAY);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            // Logo (si tu veux, place un fichier logo.png dans /resources/static)
            try {
                Image logo = Image.getInstance("src/main/resources/static/logo.png");
                logo.scaleToFit(60, 60);
                logo.setAlignment(Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception e) {
                // ignore si le logo n'existe pas
            }

            Paragraph title = new Paragraph("Contrat général WatYouFace", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Version : " + contract.getVersion(), subFont));
            document.add(new Paragraph("Date de création : " +
                    contract.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), subFont));
            document.add(Chunk.NEWLINE);

            // === 👤 Informations de l'utilisateur ===
            document.add(new Paragraph("Ce contrat est établi entre :", subFont));
            document.add(new Paragraph("WatYouFace Inc. (ci-après 'le Fournisseur')", normalFont));
            document.add(new Paragraph("et", normalFont));
            document.add(new Paragraph(user.getUsername() + " (" + user.getEmail() + "), ci-après 'l’Utilisateur'.", normalFont));
            document.add(Chunk.NEWLINE);

            // === 📜 Contenu du contrat ===
            document.add(new Paragraph("Objet du contrat", subFont));
            document.add(new Paragraph(
                    "Le présent contrat définit les conditions générales d’utilisation de la plateforme WatYouFace. " +
                    "L’Utilisateur reconnaît avoir lu et accepté sans réserve les termes du présent document.", normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Engagements de WatYouFace", subFont));
            document.add(new Paragraph(
                    "WatYouFace s’engage à protéger la confidentialité des données des utilisateurs et à garantir une " +
                    "expérience d’utilisation équitable, transparente et sécurisée.", normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Responsabilités de l’Utilisateur", subFont));
            document.add(new Paragraph(
                    "L’Utilisateur s’engage à ne pas utiliser la plateforme à des fins illégales, frauduleuses, " +
                    "ou contraires à l’éthique communautaire définie par WatYouFace.", normalFont));
            document.add(Chunk.NEWLINE);

            // === 🧾 Section personnalisée issue de la base ===
            if (contract.getContent() != null && !contract.getContent().isEmpty()) {
                document.add(new Paragraph("Contenu spécifique :", subFont));
                document.add(new Paragraph(contract.getContent(), normalFont));
                document.add(Chunk.NEWLINE);
            }

            // === 📅 Signature ===
            document.add(Chunk.NEWLINE);
            LineSeparator ls = new LineSeparator();
            document.add(new Chunk(ls));
            document.add(Chunk.NEWLINE);

            PdfPTable signatures = new PdfPTable(2);
            signatures.setWidthPercentage(100);

            PdfPCell userCell = new PdfPCell();
            userCell.setBorder(Rectangle.NO_BORDER);
            userCell.addElement(new Paragraph("Signé électroniquement par :", subFont));
            userCell.addElement(new Paragraph(user.getUsername(), normalFont));
            userCell.addElement(new Paragraph("Date : " +
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));

            PdfPCell founderCell = new PdfPCell();
            founderCell.setBorder(Rectangle.NO_BORDER);
            founderCell.addElement(new Paragraph("Fondateur :", subFont));
            founderCell.addElement(new Paragraph("Kanga Kouakou Brice", normalFont));
            founderCell.addElement(new Paragraph("WatYouFace © 2025", normalFont));

            signatures.addCell(userCell);
            signatures.addCell(founderCell);

            document.add(signatures);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
