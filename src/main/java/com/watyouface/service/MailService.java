package com.watyouface.service;

import com.watyouface.entity.User;
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
}
