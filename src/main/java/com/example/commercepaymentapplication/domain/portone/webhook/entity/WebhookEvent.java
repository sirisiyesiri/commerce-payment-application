package com.example.commercepaymentapplication.domain.portone.webhook.entity;

import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "webhook_events",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_webhook_event_webhook_id", columnNames = "webhook_id")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebhookEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "webhook_id", nullable = false, length = 200)
	private String webhookId;

	@Column(name = "event_type", nullable = false, length = 100)
	private String eventType;

	@Column(name = "portone_payment_id", length = 100)
	private String portonePaymentId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private WebhookStatus status;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(name = "processed_at")
	private LocalDateTime processedAt;

	@Column(name = "failure_reason", length = 500)
	private String failureReason;

	public WebhookEvent(String webhookId, String eventType, String portonePaymentId, String payload) {
		this.webhookId = webhookId;
		this.eventType = eventType;
		this.portonePaymentId = portonePaymentId;
		this.status = WebhookStatus.RECEIVED;
		this.payload = payload;
	}

	public void markAsProcessed() {
		this.status = WebhookStatus.PROCESSED;
		this.processedAt = LocalDateTime.now();
		this.failureReason = null;
	}

	public void markAsIgnored(String reason) {
		this.status = WebhookStatus.IGNORED;
		this.processedAt = LocalDateTime.now();
		this.failureReason = reason;
	}

	public void markAsFailed(String reason) {
		this.status = WebhookStatus.FAILED;
		this.processedAt = LocalDateTime.now();
		this.failureReason = reason;
	}
}