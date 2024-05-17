package com.enigma.laternak.service.impl;

import com.enigma.laternak.service.OtpGeneratorService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpGeneratorServiceImpl implements OtpGeneratorService {
    @Override
    public String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        String output = Integer.toString(randomNumber);
        while (output.length() < 6){
            output = "0" + output;
        }
        return output;
    }
}
