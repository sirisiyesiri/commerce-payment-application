package com.example.commercepaymentapplication.domain.order.facade;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.order.dto.*;
import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderItem;
import com.example.commercepaymentapplication.domain.order.service.OrderService;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.service.PaymentService;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.service.ProductService;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService;

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

        // 데드락 방지용 - productId가 작은 상품부터 순서대로 락 획득
        // 같은 순서로 락을 잡게 하기 위한 오름차순 정렬
        List<CartItem> orderCartItems = user.getOrderCartItems(request.cartItemIds()).stream()
                .sorted(Comparator.comparing(CartItem::getProductId))
                .toList();

        // CartItem → OrderItem 으로 변환
        List<OrderItem> orderItems = orderCartItems.stream()
                .map(cartItem -> {
                    // 주문 생성 트랜잭션 동안 상품 row를 잠가 동시 재고 차감을 막는다.
                    Product product = productService.findProductEntityForUpdate(cartItem.getProductId());

                    product.deductStock(cartItem.getQuantity());

                    return new OrderItem(
                            product,
                            product.getPrice(),
                            cartItem.getQuantity());
                })
                .toList();

        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getSubtotal)
                .sum();

        Order order = orderService.createOrder(
                user,
                orderItems,
                totalPrice,
                request.usedPointAmount()
        );

        Payment payment = paymentService.createPayment(order);

        return new AddOrderResponse(
                order.getId(),
                payment.getPortonePaymentId(),
                totalPrice,
                order.getOrderName(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }

    // 내 주문 내역 조회
    @Transactional(readOnly = true)
    public List<GetOrderResponse> getOrders(Long userId) {
        List<Order> orders = orderService.findOrderEntities(userId);

        return orders.stream()
                .map(GetOrderResponse::from)
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
