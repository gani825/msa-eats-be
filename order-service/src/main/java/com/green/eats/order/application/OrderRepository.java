package com.green.eats.order.application;

import com.green.eats.order.application.model.OrderDto;
import com.green.eats.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     커서 기반 페이지네이션으로 주문 목록 조회

     * - UserCache와 JOIN: DB 외래키 없이 JPQL에서 직접 JOIN 가능 (MSA 구조 유지)
     * - :lastId IS NULL OR o.id < :lastId: null이면 첫 페이지, 있으면 해당 ID 이전 데이터 (커서 페이지네이션)
     * - Pageable: 조회 개수 제한 (pageSize = 20)
     */
    @Query("SELECT new com.green.eats.order.application.model.OrderDto(o.id, o.totalAmount, o.status, u.name) " +
            "FROM Order o " +
            "JOIN UserCache u ON o.userId = u.userId " +
            "WHERE (:lastId IS NULL OR o.id < :lastId) " +
            "ORDER BY o.id DESC")
    List<OrderDto> findAllOrderListWithUser(@Param("lastId") Long lastId, Pageable pageable);
}