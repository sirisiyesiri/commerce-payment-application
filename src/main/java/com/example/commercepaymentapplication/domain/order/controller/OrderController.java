package com.example.commercepaymentapplication.domain.order.controller;

import com.example.commercepaymentapplication.domain.order.dto.CancelOrderResponse;
import com.example.commercepaymentapplication.domain.order.dto.GetOrderResponse;
import com.example.commercepaymentapplication.domain.order.dto.PreviewOrderResponse;
import com.example.commercepaymentapplication.domain.order.facade.OrderFacade;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        PreviewOrderResponse response = orderFacade.previewOrder(userId, cartItemIds);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetOrderResponse>>> getAllOrder(
            @AuthenticationPrincipal Long userId
    ) {
        List<GetOrderResponse> response = orderFacade.getOrders(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<GetOrderResponse>> getOneOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId
    ) {
        GetOrderResponse response = orderFacade.getOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<CancelOrderResponse>> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId
    ) {
        CancelOrderResponse response = orderFacade.cancelOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

}
