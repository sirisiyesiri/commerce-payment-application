package com.example.commercepaymentapplication.domain.cart.dto;

public record AddCartResponse(
        Long cartItemId)
{
    public static AddCartResponse of(Long cartItemId) {
        return new AddCartResponse(
                cartItemId
        );
    }
}
