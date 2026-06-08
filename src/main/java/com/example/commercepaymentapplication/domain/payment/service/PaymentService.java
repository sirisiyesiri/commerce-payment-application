package com.example.commercepaymentapplication.domain.payment.service;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentResponse;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.entity.PaymentType;
import com.example.commercepaymentapplication.domain.payment.repository.PaymentRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 결제 생성
    @Transactional
    public Payment createPayment(Order order) {
        Payment payment = new Payment(order, order.getTotalPrice(), order.getUsedPointAmount());
        return paymentRepository.save(payment);
    }

    // 주문에 연결된 결제 내역 찾기
    @Transactional(readOnly = true)
    public Payment findPaymentEntityByOrderId(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    // 주문 단건 상세 조회 : orderId만으로
    public Payment findByOrderIdWithOrder(Long orderId) {
        return paymentRepository.findByOrderIdWithOrder(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    // 결제 정보와 연관된 주문 정보를 paymentId 기반으로 조회
    public Payment findByIdWithOrder(Long paymentId) {
        return paymentRepository.findByIdWithOrder(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    // PortOne 결제 ID 기준으로 결제와 주문 관련 정보 조회
    public Payment findByPortonePaymentIdWithOrder(String portonePaymentId) {
        return paymentRepository.findByPortonePaymentIdWithOrder(portonePaymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
