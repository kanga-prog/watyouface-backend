package com.watyouface;

import com.watyouface.service.MailService;
import com.watyouface.service.PdfService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class WatYouFaceBackendApplicationTests {

    @MockBean
    private MailService mailService;

    @MockBean
    private PdfService pdfService;

    @Test
    void contextLoads() {
        // Vérifie juste que le contexte Spring démarre
    }
}
