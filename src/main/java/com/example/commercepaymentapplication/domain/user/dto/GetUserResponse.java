package com.example.commercepaymentapplication.domain.user.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;

public record GetUserResponse(
        Long userId,
        String email,
        String name,
        String phoneNumber
) {

    public static GetUserResponse from(User user) {
        return new GetUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber()
        );
    }
}