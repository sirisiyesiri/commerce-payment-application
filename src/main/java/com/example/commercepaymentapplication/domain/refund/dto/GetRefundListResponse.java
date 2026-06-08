package com.example.commercepaymentapplication.domain.refund.dto;

import java.time.LocalDateTime;

public record GetRefundListResponse (
        String refundRequestId,
        String refundedStatus,
        LocalDateTime createdAt
) {}
