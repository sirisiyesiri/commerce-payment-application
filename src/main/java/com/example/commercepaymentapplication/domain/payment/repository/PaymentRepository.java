package com.example.commercepaymentapplication.domain.payment.repository;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
