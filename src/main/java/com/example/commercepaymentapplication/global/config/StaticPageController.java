package com.example.commercepaymentapplication.global.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    @GetMapping({
            "/auth/login",
            "/auth/signup",
            "/users/me",
            "/users/me/points/transactions",
            "/carts",
            "/orders/preview",
            "/payments/confirm",
            "/products",
            "/orders",
            "/orders/{orderId}",
            "/refunds",
            "/refunds/{refundId}",
            "/products/{productId}"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
