package com.example.commercepaymentapplication.domain.refund.dto;

import java.time.LocalDateTime;

public record GetOneRefundResponse (
        Long refundId,
        String orderName,
        String reason,
        int refundedPointAmount,
        int refundedPgAmount,
        String refundedStatus,
        LocalDateTime createdAt
){}
