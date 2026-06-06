package com.example.commercepaymentapplication.domain.order.facade;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.order.dto.*;
import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderItem;
import com.example.commercepaymentapplication.domain.order.service.OrderService;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.service.PaymentService;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    // 주문서 미리보기 : 재고 차감/주문 생성 없는 읽기 전용
    @Transactional(readOnly = true)
    public PreviewOrderResponse previewOrder(Long userId, List<Long> cartItemIds){

        // cartItemsIds가 비어 있으면 "전체 장바구니", 값이 있으면 "선택된 아이템"만 주문서에 담음
        User user = userService.findUserEntityWithCartItems(userId);

        List<CartItem> orderCartItems = user.getOrderCartItems(cartItemIds);

        // CartItem을 PreviewOrderResponse.PreviewOrderItemResponse로 변환
        List<PreviewOrderItemResponse> items = orderCartItems.stream()
                .map(PreviewOrderItemResponse::from)
                .toList();

        // 장바구니 주문 총액 계산
        int totalPrice = items.stream()
                .mapToInt(PreviewOrderItemResponse::subtotal)
                .sum();

        return new PreviewOrderResponse(items, totalPrice);
    }

    // 주문 생성 + 결제 생성
    @Transactional
    public AddOrderResponse createOrder(Long userId, AddOrderRequest request) {

        // 주문한 장바구니 상품 조회
        User user = userService.findUserEntityWithCartItems(userId);

        user.validateUsablePoint(request.usedPointAmount());

        List<CartItem> orderCartItems = user.getOrderCartItems(request.cartItemIds());

        // CartItem → OrderItem 으로 변환
        List<OrderItem> orderItems = orderCartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();

                    product.deductStock(cartItem.getQuantity());

                    return OrderItem.from(cartItem);
                })
                .toList();

        int totalPrice = (orderItems.stream()
                .mapToInt(OrderItem::getSubtotal)
                .sum());

        List<Long> orderCartItemIds = orderCartItems.stream()
                .map(CartItem::getId)
                .toList();

        Order order = orderService.createOrder(
                user,
                orderItems,
                totalPrice,
                request.usedPointAmount(),
                orderCartItemIds
        );

        Payment payment = paymentService.createPayment(order);

        return new AddOrderResponse(
                order.getId(),
                payment.getPortonePaymentId(),
                payment.getPgPaymentAmount(),
                order.getOrderName(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }

    // 내 주문 내역 조회
    @Transactional(readOnly = true)
    public List<GetOrderListResponse> getOrders(Long userId) {
        List<Order> orders = orderService.findOrderEntities(userId);

        return orders.stream()
                .map(GetOrderListResponse::from)
                .toList();
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public GetOrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderService.findOrderEntity(userId, orderId);

        return GetOrderResponse.from(order);
    }

    // 주문 취소
    @Transactional
    public CancelOrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderService.findOrderEntity(userId, orderId);

        Payment payment = paymentService.findPaymentEntityByOrderId(order.getId());

        // 재고 복구
        orderService.cancelOrder(order);

        // 결제 상태 failed로 변경
        payment.markAsFailed();

        return new CancelOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                payment.getStatus(),
                order.getCanceledAt()
        );
    }
}
