package com.watyouface.service;

import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 12;
    private static final PDType1Font FONT = PDType1Font.TIMES_ROMAN;
    private static final float LEADING = 1.5f * FONT_SIZE;

    public ByteArrayInputStream generateContractPdf(Contract contract, User user) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float y = page.getMediaBox().getHeight() - MARGIN;

            // Logo
            try {
                PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/static/logo.png", document);
                content.drawImage(logo, MARGIN, y - 60, 60, 60);
            } catch (Exception e) {
                // pas de logo
            }

            y -= 80;

            // Titre
            y = addParagraph(content, MARGIN, y, "Contrat général WatYouFace", PDType1Font.HELVETICA_BOLD, 20);

            // Version et date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            y = addParagraph(content, MARGIN, y - 20,
                    "Version : " + contract.getVersion() + " | Créé le : " +
                            contract.getCreatedAt().format(dtf), FONT, 12);

            y -= 20;

            // Parties
            y = addParagraph(content, MARGIN, y, "Ce contrat est établi entre :", PDType1Font.HELVETICA_BOLD, 12);
            y = addParagraph(content, MARGIN, y - 5, "WatYouFace Inc. (ci-après 'le Fournisseur')", FONT, FONT_SIZE);
            y = addParagraph(content, MARGIN, y - 5, "et", FONT, FONT_SIZE);
            y = addParagraph(content, MARGIN, y - 5,
                    user.getUsername() + " (" + user.getEmail() + "), ci-après 'l’Utilisateur'.", FONT, FONT_SIZE);

            y -= 20;

            // Sections standard
            String[] sections = {
                    "Objet du contrat: Le présent contrat définit les conditions générales d’utilisation de la plateforme WatYouFace. L’Utilisateur reconnaît avoir lu et accepté sans réserve les termes du présent document.",
                    "Engagements de WatYouFace: WatYouFace s’engage à protéger la confidentialité des données des utilisateurs et à garantir une expérience d’utilisation équitable, transparente et sécurisée.",
                    "Responsabilités de l’Utilisateur: L’Utilisateur s’engage à ne pas utiliser la plateforme à des fins illégales, frauduleuses, ou contraires à l’éthique communautaire définie par WatYouFace."
            };

            for (String section : sections) {
                y = addWrappedParagraph(content, MARGIN, y, section, FONT, FONT_SIZE, page.getMediaBox().getWidth() - 2 * MARGIN);
                y -= 10;
            }

            // Contenu spécifique
            if (contract.getContent() != null && !contract.getContent().isEmpty()) {
                y = addParagraph(content, MARGIN, y, "Contenu spécifique :", PDType1Font.HELVETICA_BOLD, FONT_SIZE);
                y = addWrappedParagraph(content, MARGIN, y - 5, contract.getContent(), FONT, FONT_SIZE, page.getMediaBox().getWidth() - 2 * MARGIN);
            }

            // Signatures
            y -= 50;
            y = addParagraph(content, MARGIN, y, "Signatures :", PDType1Font.HELVETICA_BOLD, FONT_SIZE);
            y -= 20;
            addParagraph(content, MARGIN, y, "Utilisateur : " + user.getUsername() +
                    " | Date : " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), FONT, FONT_SIZE);
            addParagraph(content, MARGIN + 300, y, "Fondateur : Kanga Kouakou Brice | WatYouFace © 2025", FONT, FONT_SIZE);

            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private float addParagraph(PDPageContentStream content, float x, float y, String text, PDType1Font font, float fontSize) throws Exception {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - fontSize * 1.5f;
    }

    private float addWrappedParagraph(PDPageContentStream content, float x, float y, String text, PDType1Font font, float fontSize, float width) throws Exception {
        List<String> lines = wrapText(text, font, fontSize, width);
        for (String line : lines) {
            y = addParagraph(content, x, y, line, font, fontSize);
        }
        return y;
    }

    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws Exception {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float size = font.getStringWidth(testLine) / 1000 * fontSize;
            if (size > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        lines.add(line.toString());
        return lines;
    }
}
