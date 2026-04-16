package com.green.eats.order.application;

import com.green.eats.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

// Order 엔티티 전용 JPA Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}