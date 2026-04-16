package com.green.eats.order.application;

import com.green.eats.order.application.model.OrderPostReq;
import com.green.eats.order.entity.Order;
import com.green.eats.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserCacheRepository userCacheRepository;

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

        // 2. 상세 항목(List) 순회하며 추가
        req.getItems().forEach(itemReq -> {
            OrderItem item = OrderItem.builder()
                    .menuId(itemReq.getMenuId())
                    .quantity(itemReq.getQuantity())
                    .price(itemReq.getPrice())
                    .build();

            // 연관 관계 편의 메서드 활용 - order 세팅 + 리스트 추가 한 번에 처리
            order.addOrderItem(item);
        });

        return orderRepository.save(order).getId();
    }
}