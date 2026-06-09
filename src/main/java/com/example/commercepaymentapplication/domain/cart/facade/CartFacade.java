package com.example.commercepaymentapplication.domain.cart.facade;

import com.example.commercepaymentapplication.domain.cart.dto.*;
import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.service.ProductService;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public AddCartResponse addItem(Long userId, AddCartRequest request) {
        User user = userService.findUserEntity(userId);
        Product product = productService.findProductEntity(request.productId());

        CartItem cartItem = CartItem.from(user, product, request);

        return AddCartResponse.of(cartService.addItem(cartItem));
    }

    @Transactional(readOnly = true)
    public GetCartItemListResponse getCartItems(Long userId) {
        return cartService.getCartItems(userId);
    }

    @Transactional
    public CartItemDto updateCartItem(
            Long userId,
            Long cartItemId,
            UpdateCartItemQuantityRequest request) {

        User user = userService.findUserEntity(userId);

        return cartService.updateCartItemQuantity(
                user.getId(),
                cartItemId,
                request
        );
    }

    @Transactional
    public void removeOneItem(Long userId, Long cartItemId) {
        userService.findUserEntity(userId);
        cartService.removeOneItem(userId, cartItemId);
    }

    @Transactional
    public void removeAllItems(Long userId) {
        userService.findUserEntity(userId);
        cartService.removeAllItems(userId);
    }
}