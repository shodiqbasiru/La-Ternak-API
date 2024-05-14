package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.CartRequest;
import com.enigma.laternak.dto.response.CartResponse;
import com.enigma.laternak.dto.response.ImageProductResponse;
import com.enigma.laternak.entity.Cart;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.CartRepository;
import com.enigma.laternak.service.CartService;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.service.UserServiceDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final UserServiceDetail userServiceDetail;

    @Override
    public void addToCart(CartRequest request) {
        User user = userService.getById(request.getUserId());
        Product product = productService.findById(request.getProductId());

        Cart existingCart = cartRepository.findByUserAndProduct(user, product);
        if (existingCart != null) {
            existingCart.setQty(existingCart.getQty() + request.getQty());
            cartRepository.saveAndFlush(existingCart);
        }else {
            cartRepository.saveAndFlush(Cart.builder()
                    .qty(request.getQty())
                    .user(user)
                    .product(product)
                    .build());
        }
    }

    @Override
    public List<CartResponse> getAll() {
        String accountId = userServiceDetail.getByContext().getId();
        return cartRepository.findAll().stream()
                .filter(cart -> cart.getUser().getAccount().getId().equals(accountId))
                .map(cart -> CartResponse.builder()
                        .id(cart.getId())
                        .userId(cart.getUser().getId())
                        .productId(cart.getProduct().getId())
                        .productName(cart.getProduct().getProductName())
                        .price(cart.getProduct().getPrice())
                        .qty(cart.getQty())
                        .images(cart.getProduct().getImages().stream()
                                .map(image -> ImageProductResponse.builder()
                                        .name(image.getName())
                                        .url(ApiRoute.IMAGE_PRODUCT_API + "/" + image.getId())
                                        .build())
                                .toList())
                        .build()).toList();
    }

    @Override
    public Cart getById(String id) {
        return cartRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cart not found"));
    }

    @Override
    public void deleteCart(String id) {
        cartRepository.deleteAllByUserId(id);
    }

    @Override
    public void deleteCartById(String id) {
        cartRepository.deleteById(id);
    }
}
