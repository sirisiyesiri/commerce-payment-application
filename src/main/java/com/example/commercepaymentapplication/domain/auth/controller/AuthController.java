package com.example.commercepaymentapplication.domain.auth.controller;

import com.example.commercepaymentapplication.domain.auth.dto.*;
import com.example.commercepaymentapplication.domain.auth.service.AuthService;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken());
        headers.set("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(ApiResponse.ok(response));
    }
}
