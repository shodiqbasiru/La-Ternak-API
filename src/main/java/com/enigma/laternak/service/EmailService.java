package com.enigma.laternak.service;

import com.enigma.wmb_api.dto.request.EmailRequest;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(EmailRequest request) throws MessagingException;
}