package com.example.commercepaymentapplication.domain.payment.controller;

import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentRequest;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentResponse;
import com.example.commercepaymentapplication.domain.payment.facade.PaymentFacade;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ConfirmPaymentResponse>> confirmPayment(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody ConfirmPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(paymentFacade.confirmPayment(memberId, request)));
    }
}
