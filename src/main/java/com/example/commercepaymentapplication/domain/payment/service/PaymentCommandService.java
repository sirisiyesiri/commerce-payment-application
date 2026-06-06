package com.example.commercepaymentapplication.domain.payment.service;


import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.service.OrderService;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentResponse;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;

import com.example.commercepaymentapplication.domain.payment.entity.PaymentType;
import com.example.commercepaymentapplication.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PointService pointService;

    // 결제 실패
    @Transactional
    public void failPaymentAndOrder(Long orderId) {
        Payment payment = paymentService.findByOrderIdWithOrder(orderId);
        Order order = payment.getOrder();

        payment.markAsFailed();
        order.markAsCancelled();

        orderService.restoreStock(order);
    }

    @Transactional
    // POINT_ONLY 결제 시 PG사 거치지 않고 서버 내에서의 결제 확정
    public void pointOnlyPayment(Long userId, Payment payment) {
        pointService.usePoint(userId, payment, payment.getUsedPointAmount());

        // ONLY_POINT 결제 시 적립 포인트 0
        payment.markAsPaid(0);
        payment.getOrder().markAsCompleted();
    }

    // PG사를 거친 결제 확정
    @Transactional
    public void approvePaymentAndOrder(Payment payment, Order order) {
        pointService.usePoint(order.getUser().getId(), payment, payment.getUsedPointAmount());

        int earnPoint = 0;

        order.getUser().completePayment(payment.getPgPaymentAmount());

        if (payment.getType() == PaymentType.CARD_ONLY) {
            earnPoint = pointService.earnPoint(order.getUser().getId(), payment);
        }

        payment.markAsPaid(earnPoint);
        order.markAsCompleted();
    }
}
