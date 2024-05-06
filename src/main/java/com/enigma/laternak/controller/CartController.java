package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.CartRequest;
import com.enigma.laternak.dto.response.CartResponse;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.entity.Cart;
import com.enigma.laternak.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.CART_API)
@Tag(name = "Cart", description = "Cart API")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "Add To Cart",
            description = "Add Product To Cart"
    )
    @SecurityRequirement(name = "Authorization")
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

    @Operation(
            summary = "Get All Cart",
            description = "Get All Cart"
    )
    @SecurityRequirement(name = "Authorization")
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

    @Operation(
            summary = "Delete Cart",
            description = "Delete Product In Cart"
    )
    @SecurityRequirement(name = "Authorization")
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
