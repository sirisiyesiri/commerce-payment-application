package com.example.commercepaymentapplication.domain.payment.controller;

import com.example.commercepaymentapplication.domain.payment.facade.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;
}
