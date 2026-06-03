package com.example.commercepaymentapplication.domain.user.dto;

import com.example.commercepaymentapplication.domain.user.entity.MembershipGrade;
import com.example.commercepaymentapplication.domain.user.entity.User;

import java.time.LocalDateTime;

public record GetMembershipResponse(
	MembershipGrade membershipGrade,
	Integer totalPaidAmount,
	Integer amountToNextGrade,
	Integer pointRatePercent,
	LocalDateTime gradeChangedAt
) {
	public static GetMembershipResponse from(User user) {
		return new GetMembershipResponse(
			user.getMembershipGrade(),
			user.getTotalPaidAmount(),
			user.getAmountToNextGrade(),
			user.getMembershipPointRatePercent(),
			user.getGradeChangedAt()
		);
	}
}