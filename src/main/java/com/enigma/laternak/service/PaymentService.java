package com.enigma.laternak.service;

import com.enigma.laternak.entity.Order;
import com.enigma.laternak.entity.Payment;

public interface PaymentService {
    Payment createPayment(Order order);
}
