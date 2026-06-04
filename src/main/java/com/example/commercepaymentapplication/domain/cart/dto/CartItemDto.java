package com.example.commercepaymentapplication.domain.cart.dto;

public record CartItemDto(
        Long cartItemId,
        Long productId,
        String productName,
        Integer productPrice,
        Integer quantity,
        Long totalPrice
) {
}
