package com.enigma.laternak.service.impl;

import com.enigma.laternak.service.OtpGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OtpGeneratorServiceImplTest {

    private final OtpGeneratorService otpGeneratorService = new OtpGeneratorServiceImpl();

    @Test
    void shouldReturnSixDigitNumberWhenGenerateOtp() {
        String otp = otpGeneratorService.generateOtp();
        assertNotNull(otp);
        assertEquals(6, otp.length());
    }
}