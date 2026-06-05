package com.example.commercepaymentapplication.infra.portone.client;

import com.example.commercepaymentapplication.domain.payment.port.PaymentGatewayResponse;
import com.example.commercepaymentapplication.infra.portone.config.PortOneProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

class PortOneClientTest {

    private MockRestServiceServer server;
    private PortOneClient portOneClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder
                .baseUrl("https://api.portone.io")
                .build();

        PortOneProperties properties = new PortOneProperties();
        properties.setStoreId("store-test");

        portOneClient = new PortOneClient(restClient, properties);
    }

    @Test
    @DisplayName("결제 조회 시 paymentId와 storeId로 PortOne API를 호출하고 응답을 도메인 응답으로 변환한다")
    void getPayment() {
        String paymentId = "payment-123";

        server.expect(once(), requestTo("https://api.portone.io/payments/payment-123?storeId=store-test"))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        {
                          "id": "payment-123",
                          "status": "PAID",
                          "amount": {
                            "total": 15000
                          },
                          "ignoredField": "ignored"
                        }
                        """, MediaType.APPLICATION_JSON));

        PaymentGatewayResponse response = portOneClient.getPayment(paymentId);

        assertThat(response.id()).isEqualTo("payment-123");
        assertThat(response.status()).isEqualTo("PAID");
        assertThat(response.totalAmount()).isEqualTo(15000);

        server.verify();
    }

    @Test
    @DisplayName("결제 취소 시 paymentId 경로와 reason, storeId 본문으로 PortOne 취소 API를 호출한다")
    void cancelPayment() {
        String paymentId = "payment-123";

        server.expect(once(), requestTo("https://api.portone.io/payments/payment-123/cancel"))
                .andExpect(method(POST))
                .andExpect(content().json("""
                        {
                          "reason": "고객 요청 취소",
                          "storeId": "store-test"
                        }
                        """))
                .andRespond(withNoContent());

        portOneClient.cancelPayment(paymentId, "고객 요청 취소");

        server.verify();
    }
}
