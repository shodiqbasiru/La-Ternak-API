package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.CartRequest;
import com.enigma.laternak.dto.response.CartResponse;
import com.enigma.laternak.dto.response.ImageProductResponse;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.repository.CartRepository;
import com.enigma.laternak.service.CartService;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.service.UserServiceDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private UserServiceDetail userServiceDetail;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository, userService, productService, userServiceDetail);
    }

    @Test
    void shouldAddToCart() {
        CartRequest request = new CartRequest();
        request.setUserId("userId");
        request.setProductId("productId");
        request.setQty(1);

        User user = new User();
        user.setId("userId");

        Product product = new Product();
        product.setId("productId");

        Mockito.when(userService.getById(request.getUserId())).thenReturn(user);
        Mockito.when(productService.findById(request.getProductId())).thenReturn(product);

        cartService.addToCart(request);

        Mockito.verify(cartRepository).findByUserAndProduct(user, product);
        Mockito.verify(cartRepository).saveAndFlush(Mockito.any());
    }

    @Test
    void shouldAddToCartWhenExistingCartIsNotNull() {
        CartRequest request = new CartRequest();
        request.setUserId("userId");
        request.setProductId("productId");
        request.setQty(1);

        User user = new User();
        user.setId("userId");

        Product product = new Product();
        product.setId("productId");

        Cart existingCart = new Cart();
        existingCart.setQty(1);

        Mockito.when(userService.getById(request.getUserId())).thenReturn(user);
        Mockito.when(productService.findById(request.getProductId())).thenReturn(product);

        Mockito.when(cartRepository.findByUserAndProduct(user, product)).thenReturn(existingCart);

        cartService.addToCart(request);

        Mockito.verify(cartRepository).findByUserAndProduct(user, product);
        Mockito.verify(cartRepository).saveAndFlush(Mockito.any());
    }

    @Test
    void shouldReturnListCartResponse() {
        String accountId = "accountId";
        Account account = Account.builder().id(accountId).build();

        User user = new User();
        user.setId("userId");
        user.setAccount(account);

        Mockito.when(userServiceDetail.getByContext()).thenReturn(user.getAccount());

        Product product = new Product();
        product.setId("productId");
        product.setProductName("productName");
        product.setImages(
                List.of(
                        ImageProduct.builder().name("image1").build(),
                        ImageProduct.builder().name("image2").build()
                )
        );

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);

        CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .productId(cart.getProduct().getId())
                .productName(cart.getProduct().getProductName())
                .images(cart.getProduct().getImages().stream()
                        .map(image -> ImageProductResponse.builder()
                                .name(image.getName())
                                .build())
                        .toList())
                .price(cart.getProduct().getPrice())
                .qty(cart.getQty())
                .build();

        Mockito.when(cartRepository.findAll()).thenReturn(List.of(cart));

        List<CartResponse> cartResponses = cartService.getAll();

        assertEquals(1, cartResponses.size());
    }

    @Test
    void shouldReturnCartWhenGetById() {
        String id = "id";
        Cart cart = new Cart();
        cart.setId(id);

        Mockito.when(cartRepository.findById(id)).thenReturn(Optional.of(cart));

        Cart result = cartService.getById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void shouldDeleteCart() {
        String id = "id";

        Mockito.doNothing().when(cartRepository).deleteAllByUserId(id);

        cartService.deleteCart(id);

        Mockito.verify(cartRepository).deleteAllByUserId(id);
    }

    @Test
    void shouldDeleteCartById() {
        String id = "id";

        Mockito.doNothing().when(cartRepository).deleteById(id);

        cartService.deleteCartById(id);

        Mockito.verify(cartRepository).deleteById(id);

    }
}