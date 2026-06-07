package com.example.commercepaymentapplication.domain.payment.facade;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderItem;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentRequest;
import com.example.commercepaymentapplication.domain.payment.dto.ConfirmPaymentResponse;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.entity.PaymentStatus;
import com.example.commercepaymentapplication.domain.payment.entity.PaymentType;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGateway;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGatewayResponse;
import com.example.commercepaymentapplication.domain.payment.service.PaymentCommandService;
import com.example.commercepaymentapplication.domain.payment.service.PaymentService;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {

    // PortOne 결제 완료 상태값. 문자열 비교 시 매직 스트링을 피하기 위해 상수화
    private static final String PG_STATUS_PAID = "PAID";

    private final PaymentService paymentService;
    private final PaymentCommandService paymentCommandService;
    private final PaymentGateway paymentGateway;
    private final CartService cartService;

    public ConfirmPaymentResponse confirmPayment(Long userId, ConfirmPaymentRequest request) {
        // 결제 조회 + 소유자 검증
        Payment payment = paymentService.findByOrderIdWithOrder(request.orderId());
        Order order = payment.getOrder();
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 이미 처리된 결제인증 검증
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED_PAYMENT);
        }

        // 클라이언트가 보낸 portonePaymentId 와 DB값 일치 검증
        String portonePaymentId = payment.getPortonePaymentId();
        if (!portonePaymentId.equals(request.portonePaymentId())) {
            log.warn("결제 승인 거부 - portonePaymentId 불일치 : DB={}, 요청={}",
                    portonePaymentId, request.portonePaymentId());
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // POINT_ONLY 결제 시 PG사 거치지 않고 서버 내에서 결제 완료 처리
        if (payment.getType() == PaymentType.POINT_ONLY) {
            paymentCommandService.pointOnlyPayment(order.getUser().getId(), payment);

            // 장바구니 비우기
            clearOrderedCartItems(order, userId);

            return new ConfirmPaymentResponse(
                    payment.getId(),
                    payment.getOrder().getId(),
                    payment.getPgPaymentAmount(),
                    payment.getUsedPointAmount(),
                    payment.getOrder().getStatus().name(),
                    payment.getStatus().name()
            );
        }

        // PG사에서 실제 결제 정보 조회
        PaymentGatewayResponse pgPayment = paymentGateway.getPayment(portonePaymentId);

        // PortOne 상태 검증
        if (!PG_STATUS_PAID.equals(pgPayment.status())) {
            log.error("결제 승인 실패 - PG 상태 비정상 : paymentId={}, pgStatus={}",
                    payment.getId(), pgPayment.status());
            paymentCommandService.failPaymentAndOrder(order.getId());
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PAID);
        }

        // 금액 검증(금액 조작 문제 해결)
        if (payment.getPgPaymentAmount() != pgPayment.totalAmount()) {
            log.error("결제 승인 실패 - 금액 불일치 (조작 가능성) : paymentId={}, DB금액={}, PG금액={}",
                    payment.getId(), payment.getPgPaymentAmount(), pgPayment.totalAmount());
            try {
                paymentGateway.cancelPayment(portonePaymentId, "결제 금액 불일치 자동 취소");
            } catch (Exception e) {
                log.error("PG 자동 취소 실패 : 수동 처리 필요 : portonePaymentId={}", portonePaymentId, e);
            }
            paymentCommandService.failPaymentAndOrder(order.getId());
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 모든 검증 통과 → DB 상태를 최종 승인으로 전환
        paymentCommandService.approvePaymentAndOrder(payment, order);

        // 장바구니 비우기
        clearOrderedCartItems(order, userId);

        return new ConfirmPaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPgPaymentAmount(),
                payment.getUsedPointAmount(),
                payment.getOrder().getStatus().name(),
                payment.getStatus().name()
        );
    }

    private void clearOrderedCartItems(Order order, Long userId) {
        List<Long> cartItemIds = order.getOrderItems().stream()
                .map(OrderItem::getCartItemId)
                .toList();

        cartService.clearCartItems(cartItemIds, userId);
    }
}
