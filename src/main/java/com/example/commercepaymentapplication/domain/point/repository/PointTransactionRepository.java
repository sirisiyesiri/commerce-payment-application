package com.example.commercepaymentapplication.domain.point.repository;

import com.example.commercepaymentapplication.domain.point.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

	// 특정 회원의 포인트 거래 내역을 최신순으로 조회한다.
	List<PointTransaction> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}