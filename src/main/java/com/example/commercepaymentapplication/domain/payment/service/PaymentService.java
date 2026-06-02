package com.example.commercepaymentapplication.domain.payment.service;

import com.example.commercepaymentapplication.domain.payment.entity.PaymentStatus;
import com.example.commercepaymentapplication.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
}
