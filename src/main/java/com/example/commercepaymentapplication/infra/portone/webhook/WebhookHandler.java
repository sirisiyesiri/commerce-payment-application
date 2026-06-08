package com.example.commercepaymentapplication.infra.portone.webhook;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.entity.PaymentStatus;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGateway;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGatewayResponse;
import com.example.commercepaymentapplication.domain.payment.service.PaymentCommandService;
import com.example.commercepaymentapplication.domain.payment.service.PaymentService;
import com.example.commercepaymentapplication.domain.refund.service.RefundService;
import com.example.commercepaymentapplication.infra.portone.webhook.entity.WebhookEvent;
import com.example.commercepaymentapplication.infra.portone.webhook.service.WebhookEventService;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransactionCancelledCancelled;
import io.portone.sdk.server.webhook.WebhookTransactionPaid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookHandler {

	private static final String PG_STATUS_PAID = "PAID";
	private static final String PG_STATUS_CANCELLED = "CANCELLED";

	private final PaymentService paymentService;
	private final PaymentCommandService paymentCommandService;
	private final PaymentGateway paymentGateway;
	private final WebhookEventService webhookEventService;
	private final RefundService refundService;

	public void handle(String webhookId, Webhook webhook, String rawPayload) {
		String eventType = webhook.getClass().getSimpleName();
		String portonePaymentId = extractPortonePaymentId(webhook);

		// webhookId 기준으로 중복 수신 여부를 확인하고, 처음 받은 웹훅만 저장한다.
		Optional<WebhookEvent> saved = webhookEventService.saveIfNotDuplicate(
				webhookId,
				eventType,
				portonePaymentId,
				rawPayload
		);

		// 이미 처리된 webhookId라면 추가 상태 변경 없이 멱등하게 종료한다.
		if (saved.isEmpty()) {
			return;
		}

		Long eventId = saved.get().getId();

		try {
			// 결제 완료 웹훅만 실제 결제 완료 처리 대상으로 삼는다.
			if (webhook instanceof WebhookTransactionPaid paid) {
				handlePaid(eventId, paid.getData().getPaymentId());
				return;
			}

			// 결제 취소 웹훅은 환불/취소 상태 동기화 대상으로 삼는다.
			if (webhook instanceof WebhookTransactionCancelledCancelled cancelled) {
				handleCancelled(eventId, cancelled.getData().getPaymentId());
				return;
			}

			// 결제 완료/취소 외 이벤트는 저장하되 처리 대상이 아니므로 무시 상태로 기록한다.
			webhookEventService.markIgnored(eventId, "처리 대상 아님: " + eventType);
		} catch (Exception e) {
			log.error("[Webhook] failed eventId={}", eventId, e);
			webhookEventService.markFailed(eventId, e.getMessage());
		}
	}

	private void handlePaid(Long eventId, String portonePaymentId) {
		// 웹훅 본문은 신뢰하지 않고 PortOne API로 결제 정보를 직접 재조회한다.
		PaymentGatewayResponse pgPayment = paymentGateway.getPayment(portonePaymentId);

		// PortOne 결제 상태가 PAID가 아니면 결제 완료 처리하지 않는다.
		if (!PG_STATUS_PAID.equals(pgPayment.status())) {
			webhookEventService.markIgnored(eventId, "PG 상태가 PAID가 아님: " + pgPayment.status());
			return;
		}

		Payment payment = paymentService.findByPortonePaymentIdWithOrder(portonePaymentId);

		// 서버가 계산한 PG 실결제 금액과 PortOne 승인 금액이 일치하는지 검증한다.
		if (pgPayment.totalAmount() != payment.getPgPaymentAmount()) {
			log.error(
					"[Webhook] 결제 금액 불일치 portonePaymentId={}, dbAmount={}, pgAmount={}",
					portonePaymentId,
					payment.getPgPaymentAmount(),
					pgPayment.totalAmount()
			);

			try {
				paymentGateway.cancelPayment(portonePaymentId, "웹훅 결제 금액 불일치 자동 취소");
			} catch (Exception e) {
				log.error("[Webhook] 금액 불일치 보상 취소 실패 portonePaymentId={}", portonePaymentId, e);
			}

			paymentCommandService.failPaymentAndOrder(payment.getOrder().getId());
			webhookEventService.markFailed(eventId, "금액 불일치");
			return;
		}

		// Client Confirm에서 이미 완료된 결제라면 상태 변경 없이 처리 완료로 기록한다.
		if (payment.getStatus() == PaymentStatus.COMPLETED) {
			webhookEventService.markProcessed(eventId);
			return;
		}

		// 결제대기 상태가 아닌 결제는 완료 처리 대상이 아니다.
		if (payment.getStatus() != PaymentStatus.PENDING) {
			webhookEventService.markIgnored(eventId, "처리 불가능한 결제 상태: " + payment.getStatus());
			return;
		}

		// Client Confirm과 동일한 결제 완료 도메인 로직을 호출한다.
		paymentCommandService.approvePaymentAndOrder(payment.getOrder().getId());
		webhookEventService.markProcessed(eventId);
	}

	private void handleCancelled(Long eventId, String portonePaymentId) {
		// 웹훅 본문은 신뢰하지 않고 PortOne API로 결제 정보를 직접 재조회한다.
		PaymentGatewayResponse pgPayment = paymentGateway.getPayment(portonePaymentId);

		// PortOne 결제 상태가 CANCELLED가 아니면 취소 처리하지 않는다.
		if (!PG_STATUS_CANCELLED.equals(pgPayment.status())) {
			webhookEventService.markIgnored(eventId, "PG 상태가 CANCELLED가 아님: " + pgPayment.status());
			return;
		}

		Payment payment = paymentService.findByPortonePaymentIdWithOrder(portonePaymentId);

		// 환불 API에서 이미 환불 처리된 결제라면 웹훅만 처리 완료로 기록한다.
		if (payment.getStatus() == PaymentStatus.REFUND) {
			refundService.validateRefundExists(payment.getId());
			webhookEventService.markProcessed(eventId);
			return;
		}

		// PortOne 관리자 콘솔 등 외부에서 먼저 취소된 경우 서버 상태를 환불 상태로 동기화한다.
		if (payment.getStatus() == PaymentStatus.COMPLETED) {
			paymentCommandService.cancelPaymentAndOrder(
					payment.getOrder().getUser().getId(),
					payment.getId(),
					"PortOne 취소 웹훅 상태 동기화"
			);
			webhookEventService.markProcessed(eventId);
			return;
		}

		// 이미 실패/대기 등 취소 처리 대상이 아닌 상태는 무시 상태로 기록한다.
		webhookEventService.markIgnored(eventId, "처리 불가능한 결제 상태: " + payment.getStatus());
	}

	private String extractPortonePaymentId(Webhook webhook) {
		// 결제 완료 웹훅에서 PortOne 결제 ID를 추출한다.
		if (webhook instanceof WebhookTransactionPaid paid) {
			return paid.getData().getPaymentId();
		}

		// 결제 취소 웹훅에서 PortOne 결제 ID를 추출한다.
		if (webhook instanceof WebhookTransactionCancelledCancelled cancelled) {
			return cancelled.getData().getPaymentId();
		}

		return null;
	}
}