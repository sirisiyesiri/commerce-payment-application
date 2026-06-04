package com.example.commercepaymentapplication.domain.cart.controller;

import com.example.commercepaymentapplication.domain.cart.dto.AddCartRequest;
import com.example.commercepaymentapplication.domain.cart.dto.AddCartResponse;
import com.example.commercepaymentapplication.domain.cart.dto.GetCartItemListResponse;
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
}
