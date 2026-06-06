package com.example.commercepaymentapplication.domain.payment.dto;

public record ConfirmPaymentResponse(
        Long paymentId,
        Long orderId,
        int amount,
        int usedPointAmount,
        String orderStatus,
        String paymentStatus
) {}
