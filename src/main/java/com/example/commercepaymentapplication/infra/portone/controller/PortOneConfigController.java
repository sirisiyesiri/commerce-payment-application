package com.example.commercepaymentapplication.infra.portone.controller;

import com.example.commercepaymentapplication.global.response.ApiResponse;
import com.example.commercepaymentapplication.infra.portone.config.PortOneProperties;
import com.example.commercepaymentapplication.infra.portone.dto.PortOneConfigResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PortOneConfigController {

    private final PortOneProperties portOneProperties;

    @GetMapping("/api/config/portone")
    public ResponseEntity<ApiResponse<PortOneConfigResponse>> getConfig() {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(new PortOneConfigResponse(
                portOneProperties.getStoreId(),
                portOneProperties.getChannelKey()
        )));
    }
}
