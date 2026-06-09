package com.example.commercepaymentapplication.domain.payment.dto;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;

public record CancelPaymentResponse(
        Long paymentId,
        Long orderId,
        String portonePaymentId,
        String orderStatus,
        String paymentStatus
) {
    public static CancelPaymentResponse from(Payment payment) {
        return new CancelPaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPortonePaymentId(),
                payment.getOrder().getStatus().name(),
                payment.getStatus().name()
        );
    }
}
