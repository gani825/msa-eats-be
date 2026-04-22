package com.green.eats.order.application;

import com.green.eats.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Order 엔티티 전용 JPA Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // userId로 주문 목록 조회 (최신순)
    List<Order> findByUserIdOrderByIdDesc(Long userId);
}