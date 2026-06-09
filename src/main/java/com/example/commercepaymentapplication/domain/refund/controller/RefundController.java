package com.example.commercepaymentapplication.domain.refund.controller;

import com.example.commercepaymentapplication.domain.refund.dto.GetOneRefundResponse;
import com.example.commercepaymentapplication.domain.refund.dto.GetRefundListResponse;
import com.example.commercepaymentapplication.domain.refund.service.RefundService;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetRefundListResponse>>> getAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<GetRefundListResponse> response = refundService.getRefundList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }

    @GetMapping("/{refundId}")
    public ResponseEntity<ApiResponse<GetOneRefundResponse>> getOne(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long refundId
    ) {
        GetOneRefundResponse response = refundService.getOneRefund(userId, refundId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }
}
