package com.example.commercepaymentapplication.domain.payment.dto;


public record CancelPaymentResponse(
        Long paymentId,
        Long orderId,
        String portonePaymentId,
        String orderStatus,
        String paymentStatus
) {}
