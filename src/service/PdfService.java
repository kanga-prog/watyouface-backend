package com.watyouface.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.watyouface.entity.Contract;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public ByteArrayInputStream generateContractPdf(Contract contract) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Titre du document ---
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph(contract.getTitle(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // --- Métadonnées ---
            Font metaFont = new Font(Font.HELVETICA, 12, Font.ITALIC);
            document.add(new Paragraph("Version : " + contract.getVersion(), metaFont));
            document.add(new Paragraph("Date de création : " + contract.getCreatedAt(), metaFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // --- Contenu principal ---
            Font contentFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Paragraph content = new Paragraph(contract.getContent(), contentFont);
            content.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(content);

            // --- Pied de page ---
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph(
                    "© 2025 WatYouFace — Contrat d’accord général. Tous droits réservés.",
                    new Font(Font.HELVETICA, 10, Font.ITALIC)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
