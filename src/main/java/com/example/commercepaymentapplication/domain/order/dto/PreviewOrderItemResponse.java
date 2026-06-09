package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.product.entity.Product;

public record PreviewOrderItemResponse(
        Long productId,
        String productName,
        int price,
        int quantity,
        int subtotal
) {
    public static PreviewOrderItemResponse from(CartItem cartItem) {
        Product product = cartItem.getProduct();

        return new PreviewOrderItemResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity(),
                product.getPrice() * cartItem.getQuantity()
        );
    }
}
