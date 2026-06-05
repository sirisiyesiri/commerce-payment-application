package com.example.commercepaymentapplication.domain.order.service;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderItem;
import com.example.commercepaymentapplication.domain.order.repository.OrderRepository;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    @Transactional
    public Order createOrder(User user, List<OrderItem> orderItems, int totalPrice, int usedPointAmount) {
        Order order = new Order(user, totalPrice, usedPointAmount, orderItems);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    // 내 주문 목록 조회(최신순)
    public List<Order> findOrderEntities(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    // 주문 단 건 상세 조회
    public Order findOrderEntity(Long userId, Long orderId) {
        return orderRepository.findByIdWithOrderItems(userId, orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
