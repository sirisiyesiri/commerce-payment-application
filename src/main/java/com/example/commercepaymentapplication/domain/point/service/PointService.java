package com.example.commercepaymentapplication.domain.point.service;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.point.dto.GetPointResponse;
import com.example.commercepaymentapplication.domain.point.dto.GetPointTransactionResponse;
import com.example.commercepaymentapplication.domain.point.entity.PointTransaction;
import com.example.commercepaymentapplication.domain.point.entity.PointTransactionType;
import com.example.commercepaymentapplication.domain.point.repository.PointTransactionRepository;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.repository.UserRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

	private final UserRepository userRepository;
	private final PointTransactionRepository pointTransactionRepository;

	// 현재 로그인한 회원의 포인트 잔액을 조회한다.
	@Transactional(readOnly = true)
	public GetPointResponse getPointBalance(Long userId) {
		User user = findUser(userId);
		return GetPointResponse.from(user);
	}

	// 현재 로그인한 회원의 포인트 거래 내역을 최신순으로 조회한다.
	@Transactional(readOnly = true)
	public List<GetPointTransactionResponse> getPointTransactions(Long userId) {
		return pointTransactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
				.stream()
				.map(GetPointTransactionResponse::from)
				.toList();
	}

	// 결제 완료 시 사용 포인트를 차감하고 거래 내역을 기록한다.
	@Transactional
	public void usePoint(Long userId, Payment payment, int usedPointAmount) {
		if (usedPointAmount > 0) {
			User user = findUserForUpdate(userId);

			user.usePoint(usedPointAmount);
			savePointTransaction(user, payment, PointTransactionType.USED, usedPointAmount);
		}
	}

	// 결제 완료 시 포인트를 적립하고 거래 내역을 기록한다.
	@Transactional
	public int earnPoint(Long userId, Payment payment) {

		User user = findUserForUpdate(userId);

		int earnedPoint = user.earnPoint(payment.getPgPaymentAmount());

		// 적립 포인트가 있을 때만 거래 내역을 기록한다.
		if (earnedPoint > 0) {
			savePointTransaction(user, payment, PointTransactionType.EARNED, earnedPoint);
		}

		return earnedPoint;
	}

	// 환불 시 결제에 사용했던 포인트를 복구하고 거래 내역을 기록한다.
	@Transactional
	public void restoreUsedPoint(Long userId, Payment payment, int usedPointAmount) {
		if (usedPointAmount > 0) {
			User user = findUserForUpdate(userId);

			user.restoreUsedPoint(usedPointAmount);
			savePointTransaction(user, payment, PointTransactionType.USE_RESTORED, usedPointAmount);
		}
	}

	// 환불 시 결제 완료로 적립된 포인트를 회수하고 거래 내역을 기록한다.
	@Transactional
	public void revokeEarnedPoint(Long userId, Payment payment, int earnedPointAmount) {
		if (earnedPointAmount > 0) {
			User user = findUserForUpdate(userId);

			user.revokeEarnedPoint(earnedPointAmount);
			savePointTransaction(user, payment, PointTransactionType.EARN_REVOKED, earnedPointAmount);
		}
	}

	// 포인트 거래 내역을 생성하고 저장한다.
	private void savePointTransaction(User user, Payment payment, PointTransactionType pointTransactionType, int amount) {
		pointTransactionRepository.save(PointTransaction.of(user, payment, pointTransactionType, amount));
	}

	// 회원 ID로 회원을 조회하고 없으면 예외를 던진다.
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
	}

	// 포인트 잔액 변경을 위해 회원을 비관락으로 조회한다.
	private User findUserForUpdate(Long userId) {
		return userRepository.findByIdForUpdate(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
	}
}