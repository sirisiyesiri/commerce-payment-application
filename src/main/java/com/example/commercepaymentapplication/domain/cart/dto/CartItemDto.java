package com.example.commercepaymentapplication.domain.cart.dto;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.product.entity.Product;

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

    public static CartItemDto of(CartItem cartItem, Product product) {

        Long totalPrice = (long) product.getPrice() * cartItem.getQuantity();

        return new CartItemDto(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity(),
                totalPrice,
                cartItem.getCreatedAt(),
                cartItem.getCreatedAt()
        );
    }
}
