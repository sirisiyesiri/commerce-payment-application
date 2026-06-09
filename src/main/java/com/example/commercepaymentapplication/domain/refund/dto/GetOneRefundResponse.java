package com.example.commercepaymentapplication.domain.refund.dto;

import com.example.commercepaymentapplication.domain.refund.entity.Refund;

import java.time.LocalDateTime;

public record GetOneRefundResponse (
        Long refundId,
        String orderName,
        String reason,
        int refundedPointAmount,
        int refundedPgAmount,
        String refundedStatus,
        LocalDateTime createdAt
){
    public static GetOneRefundResponse from(Refund refund) {
        return new GetOneRefundResponse(
                refund.getId(),
                refund.getOrderName(),
                refund.getReason(),
                refund.getRefundedPointAmount(),
                refund.getRefundedPgAmount(),
                refund.getStatus().name(),
                refund.getCreatedAt()
        );
    }
}
