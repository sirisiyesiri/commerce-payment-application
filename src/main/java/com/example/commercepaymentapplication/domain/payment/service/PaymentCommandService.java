package com.example.commercepaymentapplication.domain.payment.service;

import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderItem;
import com.example.commercepaymentapplication.domain.order.service.OrderService;
import com.example.commercepaymentapplication.domain.payment.dto.CancelPaymentResponse;
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
    private final RefundService refundService;
    private final CartService cartService;

    // 결제 실패
    @Transactional
    public void failPaymentAndOrder(Long orderId) {
        Payment payment = paymentService.findByOrderIdWithOrder(orderId);
        Order order = payment.getOrder();

        payment.markAsFailed();
        order.markAsCancelled();

        orderService.restoreStock(order);
    }

    // POINT_ONLY 결제 시 PG사 거치지 않고 서버 내에서 결제 확정
    @Transactional
    public ConfirmPaymentResponse pointOnlyPayment(Long userId, Payment payment) {
        Order order = payment.getOrder();

        pointService.usePoint(userId, payment, payment.getUsedPointAmount());

        // POINT_ONLY 결제 시 적립 포인트 0
        payment.markAsPaid(0);
        order.markAsCompleted();

        clearOrderedCartItems(order);

        return toConfirmPaymentResponse(payment);
    }

    // PG사를 거친 결제 확정
    @Transactional
    public ConfirmPaymentResponse approvePaymentAndOrder(Long orderId) {
        Payment payment = paymentService.findByOrderIdWithOrder(orderId);
        Order order = payment.getOrder();

        pointService.usePoint(order.getUser().getId(), payment, payment.getUsedPointAmount());

        int earnPoint = 0;

        if (payment.getType() == PaymentType.CARD_ONLY) {
            earnPoint = pointService.earnPoint(order.getUser().getId(), payment);
        }

        order.getUser().completePayment(payment.getPgPaymentAmount());

        payment.markAsPaid(earnPoint);
        order.markAsCompleted();

        clearOrderedCartItems(order);

        return toConfirmPaymentResponse(payment);
    }

    // 환불
    @Transactional
    public CancelPaymentResponse cancelPaymentAndOrder(Long userId, Long paymentId, String cancelReason) {
        Payment payment = paymentService.findByIdWithOrder(paymentId);
        Order order = payment.getOrder();

        // 결제 및 주문 상태 변경
        payment.markAsRefund();
        order.markAsCancelled();

        // 포인트 갱신 : 사용분 복구 + 적립분 회수
        pointService.restoreUsedPoint(userId, payment, payment.getUsedPointAmount());
        pointService.revokeEarnedPoint(userId, payment, payment.getEarnedPointAmount());

        // 재고 복구 + 상품 상태 전이
        orderService.restoreStock(order);

        // 포인트 트랜잭션 생성
        refundService.createRefund(payment, cancelReason);

        // 누적 결제 금액 차감 → 멤버쉽 등급 재계산
        order.getUser().refundPayment(payment.getPgPaymentAmount());

        return new CancelPaymentResponse(
                paymentId,
                order.getId(),
                payment.getPortonePaymentId(),
                order.getStatus().name(),
                payment.getStatus().name()
        );
    }

    // 환불 실패
    @Transactional
    public void cancelPaymentFail(Long paymentId) {
        refundService.failedRefund(paymentId);
    }

    // 결제에 사용된 장바구니 상품을 삭제한다.
    private void clearOrderedCartItems(Order order) {
        List<Long> cartItemIds = order.getOrderItems().stream()
                .map(OrderItem::getCartItemId)
                .toList();

        cartService.clearCartItems(cartItemIds, order.getUser().getId());
    }

    private ConfirmPaymentResponse toConfirmPaymentResponse(Payment payment) {
        return new ConfirmPaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPgPaymentAmount(),
                payment.getUsedPointAmount(),
                payment.getOrder().getStatus().name(),
                payment.getStatus().name()
        );
    }
}