package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.CartRequest;
import com.enigma.laternak.dto.response.CartResponse;
import com.enigma.laternak.entity.Cart;

import java.util.List;

public interface CartService {
    void addToCart(CartRequest request);

    List<CartResponse> getAll();

    Cart getById(String id);

    void deleteCart(String id);
}
