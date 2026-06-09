package com.example.commercepaymentapplication.domain.portone.webhook;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.entity.PaymentStatus;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGateway;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGatewayResponse;
import com.example.commercepaymentapplication.domain.payment.service.PaymentCommandService;
import com.example.commercepaymentapplication.domain.payment.service.PaymentService;
import com.example.commercepaymentapplication.domain.portone.webhook.entity.WebhookEvent;
import com.example.commercepaymentapplication.domain.portone.webhook.service.WebhookEventService;
import com.example.commercepaymentapplication.domain.refund.service.RefundService;
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

		saved.ifPresent(webhookEvent ->
				processWebhook(webhookEvent.getId(), webhook, eventType)
		);
	}

	private void processWebhook(Long eventId, Webhook webhook, String eventType) {
		try {
			// 결제 완료 웹훅만 실제 결제 완료 처리 대상으로 삼는다.
			if (webhook instanceof WebhookTransactionPaid paid) {
				handlePaid(eventId, paid.getData().getPaymentId());
			} else if (webhook instanceof WebhookTransactionCancelledCancelled cancelled) {
				handleCancelled(eventId, cancelled.getData().getPaymentId());
			} else {
				webhookEventService.markIgnored(eventId, "처리 대상 아님: " + eventType);
			}
		} catch (Exception e) {
			log.error("[Webhook] failed eventId={}", eventId, e);
			webhookEventService.markFailed(eventId, e.getMessage());
		}
	}

	private void handlePaid(Long eventId, String portonePaymentId) {
		// 웹훅 본문은 신뢰하지 않고 PortOne API로 결제 정보를 직접 재조회한다.
		PaymentGatewayResponse pgPayment = paymentGateway.getPayment(portonePaymentId);

		if (PG_STATUS_PAID.equals(pgPayment.status())) {
			processPaidWebhook(eventId, portonePaymentId, pgPayment);
		} else {
			webhookEventService.markIgnored(eventId, "PG 상태가 PAID가 아님: " + pgPayment.status());
		}
	}

	private void processPaidWebhook(Long eventId, String portonePaymentId, PaymentGatewayResponse pgPayment) {
		Payment payment = paymentService.findByPortonePaymentIdWithOrder(portonePaymentId);

		if (pgPayment.totalAmount() == payment.getPgPaymentAmount()) {
			processPaidPayment(eventId, payment);
		} else {
			handlePaymentAmountMismatch(eventId, portonePaymentId, payment, pgPayment);
		}
	}

	private void processPaidPayment(Long eventId, Payment payment) {
		if (payment.getStatus() == PaymentStatus.COMPLETED) {
			webhookEventService.markProcessed(eventId);
		} else if (payment.getStatus() == PaymentStatus.PENDING) {
			paymentCommandService.approvePaymentAndOrder(payment.getOrder().getId());
			webhookEventService.markProcessed(eventId);
		} else {
			webhookEventService.markIgnored(eventId, "처리 불가능한 결제 상태: " + payment.getStatus());
		}
	}

	private void handlePaymentAmountMismatch(
			Long eventId,
			String portonePaymentId,
			Payment payment,
			PaymentGatewayResponse pgPayment
	) {
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
	}

	private void handleCancelled(Long eventId, String portonePaymentId) {
		// 웹훅 본문은 신뢰하지 않고 PortOne API로 결제 정보를 직접 재조회한다.
		PaymentGatewayResponse pgPayment = paymentGateway.getPayment(portonePaymentId);

		if (PG_STATUS_CANCELLED.equals(pgPayment.status())) {
			Payment payment = paymentService.findByPortonePaymentIdWithOrder(portonePaymentId);
			processCancelledPayment(eventId, payment);
		} else {
			webhookEventService.markIgnored(eventId, "PG 상태가 CANCELLED가 아님: " + pgPayment.status());
		}
	}

	private void processCancelledPayment(Long eventId, Payment payment) {
		if (payment.getStatus() == PaymentStatus.REFUND) {
			refundService.validateRefundExists(payment.getId());
			webhookEventService.markProcessed(eventId);
		} else if (payment.getStatus() == PaymentStatus.COMPLETED) {
			paymentCommandService.cancelPaymentAndOrder(
					payment.getOrder().getUser().getId(),
					payment.getId(),
					"PortOne 취소 웹훅 상태 동기화"
			);
			webhookEventService.markProcessed(eventId);
		} else {
			webhookEventService.markIgnored(eventId, "처리 불가능한 결제 상태: " + payment.getStatus());
		}
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

		return "";
	}
}