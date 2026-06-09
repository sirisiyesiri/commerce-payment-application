package com.example.commercepaymentapplication.domain.refund.dto;

import com.example.commercepaymentapplication.domain.refund.entity.Refund;

import java.time.LocalDateTime;

public record GetRefundListResponse (
        Long refundId,
        String orderName,
        String refundRequestId,
        String refundedStatus,
        LocalDateTime createdAt
) {
    public static GetRefundListResponse from(Refund refund) {
        return new GetRefundListResponse(
                refund.getId(),
                refund.getOrderName(),
                refund.getRefundRequestId(),
                refund.getStatus().name(),
                refund.getCreatedAt()
        );
    }
}