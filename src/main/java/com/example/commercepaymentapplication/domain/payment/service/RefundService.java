package com.example.commercepaymentapplication.domain.payment.service;


import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.entity.Refund;
import com.example.commercepaymentapplication.domain.payment.repository.RefundRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;

    @Transactional
    public void createRefund(Payment payment,String cancelReason) {
        Refund refund = new Refund(payment, cancelReason);
        refundRepository.save(refund);
    }

    @Transactional
    public void failedRefund(Long paymentId) {
        Refund refund = refundRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_NOT_FOUND));

        refund.changeRefundStatusToFail();
    }
}
