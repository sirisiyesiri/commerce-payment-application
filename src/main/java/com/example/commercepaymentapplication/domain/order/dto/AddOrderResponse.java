package com.example.commercepaymentapplication.domain.order.dto;

import java.time.LocalDateTime;

public record AddOrderResponse(
        Long orderId,
        String portonePaymentId,
        int totalPrice,
        String orderName,
        String status,
        LocalDateTime createdAt
) {}
