package com.example.commercepaymentapplication.domain.point.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;

public record GetPointResponse(
	Integer pointBalance
) {
	// 회원 엔티티에서 현재 포인트 잔액 응답을 생성한다.
	public static GetPointResponse from(User user) {
		return new GetPointResponse(user.getPointBalance());
	}
}