package com.example.commercepaymentapplication.domain.payment.repository;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder_Id(Long orderId);

    @Query("""
    SELECT p
    FROM Payment p
    JOIN FETCH p.order o
    JOIN FETCH o.user
    LEFT JOIN FETCH o.orderItems oi
    LEFT JOIN FETCH oi.product
    WHERE o.id = :orderId
""")
    Optional<Payment> findByOrderIdWithOrder(@Param("orderId") Long orderId);

}
