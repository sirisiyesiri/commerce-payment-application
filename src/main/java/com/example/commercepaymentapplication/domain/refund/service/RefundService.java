package com.example.commercepaymentapplication.domain.refund.service;


import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.refund.dto.GetOneRefundResponse;
import com.example.commercepaymentapplication.domain.refund.dto.GetRefundListResponse;
import com.example.commercepaymentapplication.domain.refund.entity.Refund;
import com.example.commercepaymentapplication.domain.refund.repository.RefundRepository;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;

    @Transactional
    public void createRefund(User user, Payment payment, String orderName, String cancelReason) {
        Refund refund = new Refund(user, payment, orderName, cancelReason);
        refundRepository.save(refund);
    }

    @Transactional
    public void failedRefund(Long paymentId) {
        Refund refund = refundRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_NOT_FOUND));

        refund.changeRefundStatusToFail();
    }

    @Transactional(readOnly = true)
    public List<GetRefundListResponse> getRefundList(Long userId) {

        List<Refund> refunds = refundRepository.findAllByUserId(userId);

        return refunds.stream()
                .map(refund -> new GetRefundListResponse(
                        refund.getId(),
                        refund.getOrderName(),
                        refund.getRefundRequestId(),
                        refund.getStatus().name(),
                        refund.getCreatedAt()
                )).toList();
    }

    @Transactional(readOnly = true)
    public GetOneRefundResponse getOneRefund(Long userId, Long refundId) {

        Refund refund = refundRepository.findByIdAndUserId(userId, refundId).orElseThrow(
                () -> new BusinessException(ErrorCode.REFUND_NOT_FOUND)
        );

        return new GetOneRefundResponse(
                refund.getId(),
                refund.getOrderName(),
                refund.getReason(),
                refund.getRefundedPointAmount(),
                refund.getRefundedPgAmount(),
                refund.getStatus().name(),
                refund.getCreatedAt()
        );
    }

    // 결제 ID 기준으로 환불 이력이 존재하는지 검증한다.
    @Transactional(readOnly = true)
    public void validateRefundExists(Long paymentId) {
        refundRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_NOT_FOUND));
    }
}
