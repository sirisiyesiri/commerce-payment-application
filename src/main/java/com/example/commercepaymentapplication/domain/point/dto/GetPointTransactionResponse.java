package com.example.commercepaymentapplication.domain.point.dto;

import com.example.commercepaymentapplication.domain.point.entity.PointTransaction;
import com.example.commercepaymentapplication.domain.point.entity.PointTransactionType;

import java.time.LocalDateTime;

public record GetPointTransactionResponse(
	Long pointTransactionId,
	Long paymentId,
	PointTransactionType pointTransactionType,
	Integer amount,
	LocalDateTime createdAt
) {
	// 포인트 거래 엔티티를 조회 응답으로 변환한다.
	public static GetPointTransactionResponse from(PointTransaction pointTransaction) {
		return new GetPointTransactionResponse(
			pointTransaction.getId(),
			pointTransaction.getPayment().getId(),
			pointTransaction.getPointTransactionType(),
			pointTransaction.getAmount(),
			pointTransaction.getCreatedAt()
		);
	}
}