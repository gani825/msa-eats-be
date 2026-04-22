package com.green.eats.order.application;

import com.green.eats.order.application.model.OrderPostReq;
import com.green.eats.order.client.StoreClient;
import com.green.eats.order.entity.Order;
import com.green.eats.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserCacheRepository userCacheRepository;
    private final StoreClient storeClient; // 재고 차감용 FeignClient

    @Transactional
    public Long postOrder(Long userId, OrderPostReq req) {
        // 유저 캐시에서 사용자 존재 여부 확인 (없으면 예외)
        userCacheRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        // 총 금액 계산 (수량 * 단가의 합계)
        Long totalAmount = req.getItems().stream()
                .mapToLong(item -> item.getQuantity() * item.getPrice())
                .sum();

        // 1. 주문 마스터 생성
        Order order = Order.builder()
                .userId( userId )
                .totalAmount( totalAmount )
                .build();


        // 2. 상세 항목 순회하며 추가 + 재고 차감
        req.getItems().forEach(itemReq -> {
            OrderItem item = OrderItem.builder()
                    .menuId(itemReq.getMenuId())
                    .quantity(itemReq.getQuantity())
                    .price(itemReq.getPrice())
                    .build();

            order.addOrderItem(item);

            // store-service에 재고 차감 요청 (FeignClient)
            storeClient.decreaseStock(itemReq.getMenuId(), itemReq.getQuantity());
        });

        return orderRepository.save(order).getId();
    }

    // 내 주문 목록 조회
    public List<Order> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByIdDesc(userId);
    }


}