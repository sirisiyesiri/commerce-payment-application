package com.example.commercepaymentapplication.domain.auth.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;

public record SignupResponse(
        Long userId,
        String email,
        String name
) {

    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
