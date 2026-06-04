package com.example.commercepaymentapplication.domain.point.service;

import com.example.commercepaymentapplication.domain.point.dto.GetPointResponse;
import com.example.commercepaymentapplication.domain.point.dto.GetPointTransactionResponse;
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

	// 회원 ID로 회원을 조회하고 없으면 예외를 던진다.
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
	}
}