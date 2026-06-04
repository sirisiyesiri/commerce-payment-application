package com.example.commercepaymentapplication.domain.cart.controller;

import com.example.commercepaymentapplication.domain.cart.dto.*;
import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.facade.CartFacade;
import com.example.commercepaymentapplication.domain.cart.repository.CartItemRepository;
import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/carts")
@RestController
public class CartController {

    private final CartFacade cartFacade;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<AddCartResponse>> addCartItem(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AddCartRequest request
    ) {
        Long cartItemId = cartFacade.addItem(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(new AddCartResponse(cartItemId)));
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
    public  ResponseEntity<ApiResponse<Void>> removeOneItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long cartItemId
    ) {
        cartFacade.removeOntItem(userId, cartItemId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
