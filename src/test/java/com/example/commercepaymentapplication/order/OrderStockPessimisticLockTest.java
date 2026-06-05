package com.example.commercepaymentapplication.domain.order;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.repository.CartItemRepository;
import com.example.commercepaymentapplication.domain.order.dto.AddOrderRequest;
import com.example.commercepaymentapplication.domain.order.facade.OrderFacade;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.entity.ProductCategory;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;
import com.example.commercepaymentapplication.domain.product.repository.ProductRepository;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.repository.UserRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class OrderStockPessimisticLockTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("동시에 같은 상품을 주문해도 실제 재고보다 많이 주문되지 않는다")
    void concurrentOrder_doesNotOversellStock() throws Exception {
        Product product = productRepository.save(new Product(
                "동시성 테스트 상품",
                10000,
                1,
                ProductStatus.ON_SALE,
                ProductCategory.FOOD,
                "재고 비관락 테스트용 상품입니다."
        ));

        User user1 = userRepository.save(User.builder()
                .email("lock-test-user1@example.com")
                .password("password")
                .name("락테스트1")
                .phoneNumber("010-1111-1111")
                .build());

        User user2 = userRepository.save(User.builder()
                .email("lock-test-user2@example.com")
                .password("password")
                .name("락테스트2")
                .phoneNumber("010-2222-2222")
                .build());

        CartItem cartItem1 = cartItemRepository.save(new CartItem(user1, product, 1));
        CartItem cartItem2 = cartItemRepository.save(new CartItem(user2, product, 1));

        int threadCount = 2;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Runnable orderRequest1 = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                orderFacade.createOrder(
                        user1.getId(),
                        new AddOrderRequest(List.of(cartItem1.getId()), 0)
                );

                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable orderRequest2 = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                orderFacade.createOrder(
                        user2.getId(),
                        new AddOrderRequest(List.of(cartItem2.getId()), 0)
                );

                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };

        executorService.submit(orderRequest1);
        executorService.submit(orderRequest2);

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        executorService.shutdown();

        Product result = productRepository.findById(product.getId()).orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(result.getStockQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("상품 row 락을 잡고 있으면 다른 트랜잭션은 락 대기 후 예외가 발생한다")
    void pessimisticLock_timeout() throws Exception {
        Product product = productRepository.save(new Product(
                "락 타임아웃 테스트 상품",
                10000,
                10,
                ProductStatus.ON_SALE,
                ProductCategory.FOOD,
                "락 타임아웃 테스트용 상품입니다."
        ));

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);

        AtomicReference<Throwable> secondThreadError = new AtomicReference<>();

        executorService.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                productRepository.findByIdForUpdate(product.getId()).orElseThrow();

                lockAcquiredLatch.countDown();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        lockAcquiredLatch.await();

        Future<?> second = executorService.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    productRepository.findByIdForUpdate(product.getId()).orElseThrow();
                } catch (Throwable e) {
                    secondThreadError.set(e);
                }
            });
        });

        second.get();
        executorService.shutdown();

        assertThat(secondThreadError.get()).isNotNull();
        assertThat(secondThreadError.get())
                .isInstanceOfAny(
                        PessimisticLockingFailureException.class,
                        CannotAcquireLockException.class,
                        JpaSystemException.class
                );
    }
}