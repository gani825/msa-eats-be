package com.green.eats.order.application;

import com.green.eats.common.exception.BusinessException;
import com.green.eats.common.exception.CommonErrorCode;
import com.green.eats.common.model.MenuGetClientRes;
import com.green.eats.order.application.model.*;
import com.green.eats.order.client.StoreClient;
import com.green.eats.order.entity.Order;
import com.green.eats.order.entity.OrderItem;
import com.green.eats.order.exception.OrderErrorCode;
import com.green.eats.order.outbox.Outbox;
import com.green.eats.order.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserCacheRepository userCacheRepository;
    private final StoreClient storeClient;
    private final OutboxRepository outboxRepository; // Outbox 패턴 추가

    @Transactional
    public Long postOrder(Long userId, OrderPostReq req) {
        // 유저 존재 여부 확인 (Kafka로 동기화된 캐시)
        userCacheRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_EXISTED_USER));

        // 서버에서 총액 재계산: (수량 × 단가)
        Integer totalAmount = req.getItems().stream()
                .mapToInt(item -> item.getQuantity() * item.getPrice())
                .sum();

        // Integer 비교는 .equals() 필수 (== 는 참조 비교라 틀림)
        if (!totalAmount.equals(req.getTotalAmount())) {
            throw BusinessException.of(OrderErrorCode.NOT_MATCHED_ALL_AMOUNT);
        }

        // 주문 마스터 생성
        Order order = Order.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .build();

        // 아이템 추가 + 재고 차감 (cascade로 OrderItem도 함께 INSERT)
        req.getItems().forEach(itemReq -> {
            OrderItem item = OrderItem.builder()
                    .menuId(itemReq.getMenuId())
                    .quantity(itemReq.getQuantity())
                    .price(itemReq.getPrice())
                    .build();
            order.addOrderItem(item);
            storeClient.decreaseStock(itemReq.getMenuId(), itemReq.getQuantity());
        });

        Long orderId = orderRepository.save(order).getId();

        // 주문 생성 이벤트를 Outbox에 저장 (같은 트랜잭션 -> 원자성 보장)
        // Kafka 장애 시에도 DB에 이벤트가 남아있어서 스케줄러가 나중에 재발행
        Outbox outbox = Outbox.builder()
                .orderId(orderId)
                .topic("order-topic")
                .payload(String.format("{\"orderId\":%d,\"userId\":%d,\"totalAmount\":%d}",
                        orderId, userId, totalAmount))
                .build();
        outboxRepository.save(outbox);

        return orderId;
    }

    // 커서 기반 페이지네이션으로 주문 목록 조회 (20개씩)
    // lastId가 null이면 첫 페이지, 있으면 해당 ID 이전 데이터 조회
    public OrderGetPageRes getOrders(Long lastId) {
        int pageSize = 20;

        List<OrderDto> orders = orderRepository.findAllOrderListWithUser(
                lastId, PageRequest.of(0, pageSize)
        );

        boolean hasNext = orders.size() == pageSize;
        Long nextLastId = orders.isEmpty() ? null : orders.get(orders.size() - 1).getOrderId();

        return new OrderGetPageRes(orders, hasNext, nextLastId);
    }

    // 주문 상세 조회: menuId -> store-service 일괄 조회 -> 메뉴명 매핑
    public List<OrderGetDetailRes> getOrderDetail(Long orderId) {
        List<OrderItem> orderList = orderItemRepository.findAllByOrderId(orderId);

        // 중복 제거 후 menuId 목록 추출
        List<Long> menuIds = orderList.stream()
                .map(OrderItem::getMenuId)
                .distinct()
                .toList();

        // store-service 일괄 요청 -> Map<menuId, MenuGetClientRes> (O(1) 접근)
        Map<Long, MenuGetClientRes> menuMap = storeClient.getMenuDetail(menuIds);

        return orderList.stream().map(item -> OrderGetDetailRes.builder()
                .id(item.getId())
                .name(menuMap.get(item.getMenuId()).getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build()
        ).toList();
    }
}