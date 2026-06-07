package com.example.commercepaymentapplication.domain.payment.facade;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.payment.dto.CancelPaymentRequest;
import com.example.commercepaymentapplication.domain.payment.dto.CancelPaymentResponse;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {

    // PortOne 결제 완료 상태값. 문자열 비교 시 매직 스트링을 피하기 위해 상수화한다.
    private static final String PG_STATUS_PAID = "PAID";

    private final PaymentService paymentService;
    private final PaymentCommandService paymentCommandService;
    private final PaymentGateway paymentGateway;

    // 클라이언트 결제 완료 콜백 이후 서버에서 결제를 확정한다.
    public ConfirmPaymentResponse confirmPayment(Long userId, ConfirmPaymentRequest request) {
        // 결제 조회 + 주문/회원 정보 함께 조회
        Payment payment = paymentService.findByOrderIdWithOrder(request.orderId());
        Order order = payment.getOrder();

        // 본인 주문/결제인지 소유권을 검증한다.
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 클라이언트가 보낸 portonePaymentId와 서버에 저장된 portonePaymentId가 일치하는지 검증한다.
        String portonePaymentId = payment.getPortonePaymentId();
        if (!portonePaymentId.equals(request.portonePaymentId())) {
            log.warn("결제 승인 거부 - portonePaymentId 불일치 : DB={}, 요청={}",
                    portonePaymentId, request.portonePaymentId());
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // 이미 결제 완료된 요청은 중복 확정 요청으로 보고 멱등하게 성공 응답을 반환한다.
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return toConfirmPaymentResponse(payment);
        }

        // 결제 대기 상태가 아니면 결제 확정 처리가 불가능하다.
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED_PAYMENT);
        }

        // 포인트 전액 결제는 PG 호출 없이 서버 내부에서 바로 결제 완료 처리한다.
        if (payment.getType() == PaymentType.POINT_ONLY) {
            return paymentCommandService.pointOnlyPayment(order.getUser().getId(), payment);
        }

        // PortOne API로 실제 결제 정보를 조회한다. 클라이언트가 보낸 결제 결과는 그대로 신뢰하지 않는다.
        PaymentGatewayResponse pgPayment = paymentGateway.getPayment(portonePaymentId);

        // PortOne 결제 상태가 결제 완료 상태인지 검증한다.
        if (!PG_STATUS_PAID.equals(pgPayment.status())) {
            log.error("결제 승인 실패 - PG 상태 비정상 : paymentId={}, pgStatus={}",
                    payment.getId(), pgPayment.status());

            // PG에서 결제가 완료되지 않은 경우 주문/결제를 실패 처리하고 선차감 재고를 복구한다.
            paymentCommandService.failPaymentAndOrder(order.getId());
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PAID);
        }

        // 서버가 산정한 PG 실결제 금액과 PortOne 승인 금액이 정확히 일치하는지 검증한다.
        if (payment.getPgPaymentAmount() != pgPayment.totalAmount()) {
            log.error("결제 승인 실패 - 금액 불일치 : paymentId={}, DB금액={}, PG금액={}",
                    payment.getId(), payment.getPgPaymentAmount(), pgPayment.totalAmount());

            // 외부 결제는 성공했지만 내부 검증이 실패한 경우 PortOne 결제를 보상 취소한다.
            try {
                paymentGateway.cancelPayment(portonePaymentId, "결제 금액 불일치 자동 취소");
            } catch (Exception e) {
                log.error("PG 자동 취소 실패 : 수동 처리 필요 : portonePaymentId={}", portonePaymentId, e);
            }

            // 내부 주문/결제는 실패 처리하고 선차감 재고를 복구한다.
            paymentCommandService.failPaymentAndOrder(order.getId());
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 모든 검증이 통과되면 공통 결제 완료 트랜잭션을 호출한다.
        return paymentCommandService.approvePaymentAndOrder(order.getId());
    }

    // 결제 완료된 주문에 대해 전액 환불을 요청한다.
    public CancelPaymentResponse cancelPayment(Long userId, Long paymentId, CancelPaymentRequest request) {
        // 결제 조회 + 주문/회원 정보 함께 조회
        Payment payment = paymentService.findByIdWithOrder(paymentId);

        // 본인 결제인지 소유권을 검증한다.
        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 결제 완료 상태만 환불 가능하다.
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        // DB 상태 변경, 재고 복구, 포인트 복구/회수, 환불 이력 생성을 트랜잭션으로 처리한다.
        CancelPaymentResponse response = paymentCommandService.cancelPaymentAndOrder(
                userId,
                paymentId,
                request.reason()
        );

        // DB 반영 이후 PortOne PG 취소 API를 호출한다.
        try {
            paymentGateway.cancelPayment(response.portonePaymentId(), request.reason());
        } catch (Exception e) {
            log.error("PG 결제 취소 실패 : DB는 이미 환불 처리됨, 수동 처리 필요 : portonePaymentId={}",
                    response.portonePaymentId(), e);

            // PG 취소 실패 시 환불 이력을 실패 상태로 표시한다.
            paymentCommandService.cancelPaymentFail(payment.getId());
        }

        return response;
    }

    // 결제 확정 응답 DTO를 생성한다.
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