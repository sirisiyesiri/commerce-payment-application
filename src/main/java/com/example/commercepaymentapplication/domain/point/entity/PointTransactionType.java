package com.example.commercepaymentapplication.domain.point.entity;

public enum PointTransactionType {
	USED(-1),
	EARNED(1),
	USE_RESTORED(1),
	EARN_REVOKED(-1);

	private final int sign;

	PointTransactionType(int sign) {
		this.sign = sign;
	}

	// 거래 타입에 맞는 금액 부호를 적용한다.
	public int applySign(int amount) {
		return Math.abs(amount) * sign;
	}
}