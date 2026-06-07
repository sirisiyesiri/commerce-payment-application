package com.example.commercepaymentapplication.domain.payment.controller;

import com.example.commercepaymentapplication.domain.payment.dto.CancelPaymentRequest;
import com.example.commercepaymentapplication.domain.payment.dto.CancelPaymentResponse;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentRequest;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentResponse;
import com.example.commercepaymentapplication.domain.payment.facade.PaymentFacade;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ConfirmPaymentResponse>> confirmPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ConfirmPaymentRequest request) {
        ConfirmPaymentResponse response = paymentFacade.confirmPayment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/{paymentId}/refunds")
    public ResponseEntity<ApiResponse<CancelPaymentResponse>> cancelPayment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long paymentId,
            @Valid @RequestBody CancelPaymentRequest request
            ) {
        CancelPaymentResponse response = paymentFacade.cancelPayment(userId, paymentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }
}
