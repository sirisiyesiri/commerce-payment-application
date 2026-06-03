package com.example.commercepaymentapplication.domain.cart.controller;

import com.example.commercepaymentapplication.domain.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/carts")
@RestController
public class CartController {

    private final CartItemRepository cartItemRepository;
}
