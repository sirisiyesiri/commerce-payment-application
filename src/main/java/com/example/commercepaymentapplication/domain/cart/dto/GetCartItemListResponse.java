package com.example.commercepaymentapplication.domain.cart.dto;


import java.util.List;

public record GetCartItemListResponse(
        List<CartItemDto> cartItemList,
        Long totalAmount
) {}
