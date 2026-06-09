package com.example.commercepaymentapplication.domain.portone.client;

import com.example.commercepaymentapplication.domain.payment.port.PaymentGateway;
import com.example.commercepaymentapplication.domain.payment.port.PaymentGatewayResponse;
import com.example.commercepaymentapplication.global.config.PortOneProperties;
import com.example.commercepaymentapplication.domain.portone.dto.PortOneCancelRequest;
import com.example.commercepaymentapplication.domain.portone.dto.PortOnePaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortOneClient implements PaymentGateway {

    private final RestClient portOneRestClient;
    private final PortOneProperties portOneProperties;

    @Override
    public PaymentGatewayResponse getPayment(String paymentId) {
        log.info("PortOne 결제 조회: {}", paymentId);

        PortOnePaymentResponse response = portOneRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments/{paymentId}")
                        .queryParam("storeId", portOneProperties.getStoreId())
                        .build(paymentId))
                .retrieve()
                .body(PortOnePaymentResponse.class);

        return PaymentGatewayResponse.of(
                response.id(),
                response.status(),
                response.amount().total()
        );
    }

    @Override
    public void cancelPayment(String paymentId, String reason) {
        // paymentId logging
        log.info("PortOne 결제 취소 요청: paymentId={}, reason={}", paymentId, reason);

        portOneRestClient.post()
                .uri("/payments/{paymentId}/cancel", paymentId)
                .body(PortOneCancelRequest.of(reason, portOneProperties.getStoreId()))
                .retrieve()
                .toBodilessEntity();
    }
}