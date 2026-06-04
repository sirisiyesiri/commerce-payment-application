package com.example.commercepaymentapplication.domain.cart.dto;


import java.util.List;

public record GetCartItemListResponse(
        List<CartItemDto> cartItemList,
        Long totalAmount
) {

    public record CartItemDto(
            Long cartItemId,
            Long productId,
            String productName,
            Integer productPrice,
            Integer quantity,
            Long totalPrice
    ) {
    }
}
