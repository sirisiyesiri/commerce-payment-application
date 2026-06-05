package com.example.commercepaymentapplication.infra.portone.controller;

import com.example.commercepaymentapplication.global.response.ApiResponse;
import com.example.commercepaymentapplication.infra.portone.config.PortOneProperties;
import com.example.commercepaymentapplication.infra.portone.dto.PortOneConfigResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PortOneConfigControllerTest {

    @Test
    @DisplayName("PortOne 공개 설정 조회 시 storeId와 channelKey만 응답한다")
    void getConfig() {
        PortOneProperties properties = new PortOneProperties();
        properties.setStoreId("store-test");
        properties.setChannelKey("channel-test");
        properties.setApiSecret("secret-should-not-be-exposed");
        properties.setBaseUrl("https://api.portone.io");

        PortOneConfigController controller = new PortOneConfigController(properties);

        ResponseEntity<ApiResponse<PortOneConfigResponse>> response = controller.getConfig();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().storeId()).isEqualTo("store-test");
        assertThat(response.getBody().getData().channelKey()).isEqualTo("channel-test");
    }
}
