package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.EmailRequest;
import com.enigma.laternak.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(javaMailSender);
    }

    @Test
    void shouldSendEmail() throws MessagingException {
        EmailRequest request = new EmailRequest();
        request.setTo("to");
        request.setSubject("subject");
        request.setBody("body");

        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessageHelper messageHelper = Mockito.mock(MimeMessageHelper.class);
        Mockito.when(new MimeMessageHelper(mimeMessage)).thenReturn(messageHelper);

        assertDoesNotThrow(() -> emailService.sendEmail(request));
    }
}