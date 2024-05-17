package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.EmailRequest;
import com.enigma.laternak.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailRequest request) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setTo(request.getTo());
        messageHelper.setSubject(request.getSubject());
        messageHelper.setText(request.getBody(), true);
        javaMailSender.send(mimeMessage);
    }
}
