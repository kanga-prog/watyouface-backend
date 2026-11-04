package com.watyouface;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.watyouface.exception.StepUpDeliveryException;
import com.watyouface.security.StepUpAction;
import com.watyouface.service.MailService;
import com.watyouface.service.SmtpStepUpCodeSender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class SmtpStepUpCodeSenderTests {

    @Test
    void preservesStepUpDeliveryExceptionAndLogsSafeTechnicalCause() throws Exception {
        MailService mailService = mock(MailService.class);
        doThrow(new IllegalStateException("delivery wrapper", new IOException("SMTP connection timed out")))
                .when(mailService)
                .sendStepUpCode("recipient@example.test", StepUpAction.PASSWORD_CHANGE, "123456");
        SmtpStepUpCodeSender sender = new SmtpStepUpCodeSender(mailService);

        List<ILoggingEvent> events = captureLogs(() ->
                assertThatThrownBy(() -> sender.send("recipient@example.test", StepUpAction.PASSWORD_CHANGE, "123456"))
                        .isInstanceOf(StepUpDeliveryException.class));

        assertThat(events).singleElement().satisfies(event -> {
            assertThat(event.getFormattedMessage()).contains(
                    "action=PASSWORD_CHANGE",
                    "exceptionClass=IllegalStateException",
                    "rootCauseClass=IOException",
                    "rootCauseMessage=SMTP connection timed out");
        });
    }

    @Test
    void suppressesSensitiveDeliveryDetailsFromLogs() throws Exception {
        String recipient = "recipient@example.test";
        String code = "654321";
        String secret = "smtp-password-should-not-log";
        MailService mailService = mock(MailService.class);
        doThrow(new IllegalStateException("wrapper", new IOException(
                "SMTP rejected " + recipient + " with password " + secret + " and code " + code)))
                .when(mailService)
                .sendStepUpCode(recipient, StepUpAction.EMAIL_CHANGE, code);
        SmtpStepUpCodeSender sender = new SmtpStepUpCodeSender(mailService);

        List<ILoggingEvent> events = captureLogs(() ->
                assertThatThrownBy(() -> sender.send(recipient, StepUpAction.EMAIL_CHANGE, code))
                        .isInstanceOf(StepUpDeliveryException.class));

        assertThat(events).singleElement().satisfies(event -> {
            String message = event.getFormattedMessage();
            assertThat(message).contains("action=EMAIL_CHANGE", "rootCauseClass=IOException");
            assertThat(message).doesNotContain(recipient, code, secret, "rootCauseMessage=");
        });
    }

    private List<ILoggingEvent> captureLogs(ThrowingRunnable action) throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(SmtpStepUpCodeSender.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            action.run();
            return List.copyOf(appender.list);
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
