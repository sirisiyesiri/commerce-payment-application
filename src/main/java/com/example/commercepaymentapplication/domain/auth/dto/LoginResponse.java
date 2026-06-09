package com.example.commercepaymentapplication.domain.auth.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;

public record LoginResponse(
        Long userId,
        String email,
        String name,
        String token
) {

    public static LoginResponse from(User user, String token) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                token
        );
    }
}
