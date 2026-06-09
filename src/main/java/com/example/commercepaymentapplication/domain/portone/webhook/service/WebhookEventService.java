package com.example.commercepaymentapplication.domain.portone.webhook.service;

import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import com.example.commercepaymentapplication.domain.portone.webhook.entity.WebhookEvent;
import com.example.commercepaymentapplication.domain.portone.webhook.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WebhookEventService {

	private final WebhookEventRepository webhookEventRepository;

	// 중복이 아니면 RECEIVED 상태로 저장하고, 중복이면 Optional.empty()를 반환한다.
	public Optional<WebhookEvent> saveIfNotDuplicate(
			String webhookId,
			String eventType,
			String portonePaymentId,
			String payload
	) {
		if (webhookEventRepository.existsByWebhookId(webhookId)) {
			return Optional.empty();
		}

		return Optional.of(
				webhookEventRepository.save(
						WebhookEvent.of(webhookId, eventType, portonePaymentId, payload)
				)
		);
	}

	// 웹훅 본 처리 로직이 성공한 경우 처리 완료 상태로 변경한다.
	public void markProcessed(Long eventId) {
		load(eventId).markAsProcessed();
	}

	// 처리 대상이 아닌 이벤트이거나 이미 처리된 결제인 경우 무시 상태로 변경한다.
	public void markIgnored(Long eventId, String reason) {
		load(eventId).markAsIgnored(reason);
	}

	// 본 처리 중 예외가 발생한 경우 실패 상태로 변경한다.
	public void markFailed(Long eventId, String reason) {
		load(eventId).markAsFailed(reason);
	}

	// 웹훅 이벤트를 ID로 조회하고 없으면 예외를 던진다.
	private WebhookEvent load(Long eventId) {
		return webhookEventRepository.findById(eventId)
				.orElseThrow(() -> new BusinessException(ErrorCode.WEBHOOK_EVENT_NOT_FOUND));
	}
}