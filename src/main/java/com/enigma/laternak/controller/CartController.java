package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.CartRequest;
import com.enigma.laternak.dto.response.CartResponse;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.entity.Cart;
import com.enigma.laternak.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.CART_API)
public class CartController {

    private final CartService cartService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<CartRequest>> addToCart(@RequestBody CartRequest request) {
        Cart cart = cartService.addToCart(request);
        return ResponseEntity.ok(CommonResponse.<CartRequest>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Added to cart successfully")
                .data(CartRequest.builder()
                        .userId(cart.getUser().getId())
                        .productId(cart.getProduct().getId())
                        .qty(cart.getQty())
                        .build())
                .build());
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<CartResponse>>> getAll() {
        List<CartResponse> carts = cartService.getAll();
        return ResponseEntity.ok(CommonResponse.<List<CartResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(carts)
                .build());
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<String>> deleteCart(@PathVariable String id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok(CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Deleted successfully")
                .data(id)
                .build());
    }
}
