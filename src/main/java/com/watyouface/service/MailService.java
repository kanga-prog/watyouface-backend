package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.security.StepUpAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendContractEmail(User user, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("Votre contrat WatYouFace signé");
        helper.setText("Bonjour " + user.getUsername() + ",\n\nVoici votre contrat signé.\n\nMerci de votre confiance ❤️", false);
        helper.addAttachment("Contrat_WatYouFace.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    public void sendStepUpCode(String recipient, StepUpAction action, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setTo(recipient);
        helper.setSubject("Code de vérification WatYouFace");
        helper.setText("Votre code temporaire pour " + actionLabel(action) + " est : " + code
                + "\n\nCe code a une durée de validité courte et ne peut être utilisé qu’une seule fois."
                + " Si vous n’êtes pas à l’origine de cette demande, ignorez ce message.", false);
        mailSender.send(message);
    }

    private String actionLabel(StepUpAction action) {
        return action == StepUpAction.EMAIL_CHANGE ? "modifier l’adresse e-mail" : "modifier le mot de passe";
    }
}
