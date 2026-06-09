package com.example.commercepaymentapplication.domain.cart.dto;


import java.util.List;

public record GetCartItemListResponse(
        List<CartItemDto> cartItemList,
        Long totalAmount
) {
    public static GetCartItemListResponse of(
            List<CartItemDto> cartItemList,
            Long totalAmount
    ) {
        return new GetCartItemListResponse(
                cartItemList,
                totalAmount
        );
    }
}
