package com.example.commercepaymentapplication.domain.cart.dto;

import java.time.LocalDateTime;

public record CartItemDto(
        Long cartItemId,
        Long productId,
        String productName,
        Integer productPrice,
        Integer quantity,
        Long totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
