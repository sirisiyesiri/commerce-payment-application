package com.example.commercepaymentapplication.domain.order.controller;

import com.example.commercepaymentapplication.domain.order.dto.PreviewOrderResponse;
import com.example.commercepaymentapplication.domain.order.facade.OrderFacade;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderFacade orderFacade;

    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<PreviewOrderResponse>> preview(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false)List<Long> cartItemIds
            ) {
        ApiResponse<PreviewOrderResponse> response = ApiResponse.ok(orderFacade.previewOrder(userId, cartItemIds));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
