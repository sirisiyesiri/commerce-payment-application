package com.example.commercepaymentapplication.domain.point.entity;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@Enumerated(EnumType.STRING)
	@Column(name = "point_transaction_type", nullable = false)
	private PointTransactionType pointTransactionType;

	@Column(nullable = false)
	private Integer amount;

	private PointTransaction(User user, Payment payment, PointTransactionType pointTransactionType, Integer amount) {
		this.user = user;
		this.payment = payment;
		this.pointTransactionType = pointTransactionType;
		this.amount = amount;
	}

	// 포인트 거래를 생성한다.
	public static PointTransaction of(User user, Payment payment, PointTransactionType pointTransactionType, Integer amount) {
		validateAmount(amount);

		return new PointTransaction(
			user,
			payment,
			pointTransactionType,
			pointTransactionType.applySign(amount)
		);
	}

	// 유효하지 않은 포인트 거래 금액이면 예외를 던진다.
	private static void validateAmount(Integer amount) {
		if (amount == null || amount <= 0) {
			throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
		}
	}
}