package com.example.commercepaymentapplication.domain.refund.dto;

import java.time.LocalDateTime;

public record GetRefundListResponse (
        Long refundId,
        String orderName,
        String refundRequestId,
        String refundedStatus,
        LocalDateTime createdAt
) {}