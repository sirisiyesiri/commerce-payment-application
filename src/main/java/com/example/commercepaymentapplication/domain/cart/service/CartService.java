package com.example.commercepaymentapplication.domain.cart.service;

import com.example.commercepaymentapplication.domain.cart.repository.CartItemRepository;
import com.example.commercepaymentapplication.domain.product.repository.ProductRepository;
import com.example.commercepaymentapplication.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
}
