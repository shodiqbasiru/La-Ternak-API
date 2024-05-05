package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.CartRequest;
import com.enigma.laternak.dto.response.CartResponse;
import com.enigma.laternak.entity.Cart;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.CartRepository;
import com.enigma.laternak.service.CartService;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    public Cart addToCart(CartRequest request) {
        User user = userService.getById(request.getUserId());
        Product product = productService.findById(request.getProductId());
        return cartRepository.saveAndFlush(Cart.builder()
                .qty(request.getQty())
                .user(user)
                .product(product)
                .build());
    }

    @Override
    public List<CartResponse> getAll() {
        return cartRepository.findAll().stream().map(cart -> CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .productId(cart.getProduct().getId())
                .qty(cart.getQty())
                .build()).toList();
    }

    @Override
    public void deleteCart(String id) {
        cartRepository.deleteById(id);
    }
}
