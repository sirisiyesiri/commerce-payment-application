package com.example.commercepaymentapplication.domain.point.controller;

import com.example.commercepaymentapplication.domain.point.dto.GetPointResponse;
import com.example.commercepaymentapplication.domain.point.dto.GetPointTransactionResponse;
import com.example.commercepaymentapplication.domain.point.service.PointService;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/points")
@RequiredArgsConstructor
public class PointController {

	private final PointService pointService;

	// 현재 로그인한 회원의 포인트 잔액을 조회한다.
	@GetMapping
	public ResponseEntity<ApiResponse<GetPointResponse>> getPointBalance(
			@AuthenticationPrincipal Long userId
	) {
		GetPointResponse response = pointService.getPointBalance(userId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.ok(response));
	}

	// 현재 로그인한 회원의 포인트 거래 내역을 최신순으로 조회한다.
	@GetMapping("/transactions")
	public ResponseEntity<ApiResponse<List<GetPointTransactionResponse>>> getPointTransactions(
			@AuthenticationPrincipal Long userId
	) {
		List<GetPointTransactionResponse> response = pointService.getPointTransactions(userId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.ok(response));
	}
}