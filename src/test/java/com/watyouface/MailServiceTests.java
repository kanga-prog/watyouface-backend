package com.watyouface;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.watyouface.entity.User;
import com.watyouface.security.StepUpAction;
import com.watyouface.service.MailService;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MailServiceTests {

    private static final String FROM_ADDRESS = "no-reply@watyouface.com";
    private static final String FROM_NAME = "WatYouFace";

    @Test
    void sendsContractFromConfiguredSenderToUserWithExpectedSubject() throws Exception {
        JavaMailSender sender = senderWithInMemoryMessages();
        MailService service = new MailService(sender, FROM_ADDRESS, FROM_NAME);
        User user = new User();
        user.setUsername("alice");
        user.setEmail("alice@example.test");

        service.sendContractEmail(user, new byte[]{1, 2, 3});

        MimeMessage message = sentMessage(sender);
        assertSender(message);
        assertRecipient(message, "alice@example.test");
        assertThat(message.getSubject()).isEqualTo("Votre contrat WatYouFace signé");
    }

    @Test
    void sendsStepUpCodeInBodyWithoutLoggingIt() throws Exception {
        JavaMailSender sender = senderWithInMemoryMessages();
        MailService service = new MailService(sender, FROM_ADDRESS, FROM_NAME);
        String code = "123456";
        Logger logger = (Logger) LoggerFactory.getLogger(MailService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            service.sendStepUpCode("security@example.test", StepUpAction.PASSWORD_CHANGE, code);
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }

        MimeMessage message = sentMessage(sender);
        assertSender(message);
        assertRecipient(message, "security@example.test");
        assertThat(message.getSubject()).isEqualTo("Code de vérification WatYouFace");
        assertThat((String) message.getContent()).contains(code);
        assertThat(appender.list)
                .noneMatch(event -> event.getFormattedMessage().contains(code));
    }

    private JavaMailSender senderWithInMemoryMessages() {
        JavaMailSender sender = mock(JavaMailSender.class);
        when(sender.createMimeMessage()).thenAnswer(invocation ->
                new MimeMessage(Session.getInstance(new Properties())));
        return sender;
    }

    private MimeMessage sentMessage(JavaMailSender sender) {
        org.mockito.ArgumentCaptor<MimeMessage> captor = org.mockito.ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender).send(captor.capture());
        return captor.getValue();
    }

    private void assertSender(MimeMessage message) throws Exception {
        Address[] from = message.getFrom();
        assertThat(from).hasSize(1);
        InternetAddress sender = (InternetAddress) from[0];
        assertThat(sender.getAddress()).isEqualTo(FROM_ADDRESS);
        assertThat(sender.getPersonal()).isEqualTo(FROM_NAME);
    }

    private void assertRecipient(MimeMessage message, String expectedRecipient) throws Exception {
        Address[] recipients = message.getRecipients(MimeMessage.RecipientType.TO);
        assertThat(recipients).hasSize(1);
        assertThat(((InternetAddress) recipients[0]).getAddress()).isEqualTo(expectedRecipient);
    }
}
