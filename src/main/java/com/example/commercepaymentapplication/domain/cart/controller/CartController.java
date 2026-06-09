package com.example.commercepaymentapplication.domain.cart.controller;

import com.example.commercepaymentapplication.domain.cart.dto.*;
import com.example.commercepaymentapplication.domain.cart.facade.CartFacade;
import com.example.commercepaymentapplication.domain.order.dto.AddOrderRequest;
import com.example.commercepaymentapplication.domain.order.dto.AddOrderResponse;
import com.example.commercepaymentapplication.domain.order.facade.OrderFacade;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/carts")
@RestController
public class CartController {

    private final CartFacade cartFacade;
    private final OrderFacade orderFacade;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<AddCartResponse>> addCartItem(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddCartRequest request
    ) {
        AddCartResponse response = cartFacade.addItem(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetCartItemListResponse>> getCartItem(
            @AuthenticationPrincipal Long userId
    ) {
        GetCartItemListResponse response = cartFacade.getCartItems(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(response));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemDto>> updateCartItemQuantity(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request
            ) {
        CartItemDto response = cartFacade.updateCartItem(userId, cartItemId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(response));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeOneItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long cartItemId
    ) {
        cartFacade.removeOneItem(userId, cartItemId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<Void>> removeAllItems(
            @AuthenticationPrincipal Long userId
    ) {
        cartFacade.removeAllItems(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<AddOrderResponse>> createCartItemOrder(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddOrderRequest request
    ) {
        AddOrderResponse response = orderFacade.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }
}
