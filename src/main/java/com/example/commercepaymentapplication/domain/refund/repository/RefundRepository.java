package com.example.commercepaymentapplication.domain.refund.repository;

import com.example.commercepaymentapplication.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    @Query("SELECT r FROM Refund r JOIN FETCH r.payment p WHERE p.id = :paymentId")
    Optional<Refund> findByPaymentId (@Param("paymentId") Long paymentId);

    @Query("SELECT r FROM Refund r WHERE r.user.id = :userId")
    List<Refund> findAllByUserId (@Param("userId") Long userId);

    @Query("SELECT r FROM Refund r WHERE r.user.id = :userId AND r.id = :refundId")
    Optional<Refund> findByIdAndUserId (@Param("userId") Long userId, @Param("refundId") Long refundId);
}
