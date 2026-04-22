package com.green.eats.order.application;

import com.green.eats.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 OrderItem 전용 JPA Repository
 - 주문 상세 조회 시 orderId로 아이템 목록 가져올 때 사용
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Spring Data JPA 네이밍 규칙: findAllBy + 필드명 → WHERE order_id = ?
    List<OrderItem> findAllByOrderId(Long orderId);
}