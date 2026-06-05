package com.example.commercepaymentapplication.domain.order.service;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderItem;
import com.example.commercepaymentapplication.domain.order.entity.OrderStatus;
import com.example.commercepaymentapplication.domain.order.repository.OrderRepository;
import com.example.commercepaymentapplication.domain.product.entity.Product;
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

    @Transactional
    public void cancelOrder(Order order) {
        // 결제 대기만 취소 가능
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 주문 상태 변경
        order.markAsCancelled();

        // 재고 복구
        for(OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.restoreStock(orderItem.getQuantity());
        }
    }
}
