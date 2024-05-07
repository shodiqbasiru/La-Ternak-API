package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}
