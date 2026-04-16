package com.green.eats.order.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @Tsid
    private Long id;

    // user_cache 테이블의 userId를 참조 (FK 대신 값으로만 참조)
    @Column(nullable = false)
    private Long userId;

    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 주문 상세 항목 목록 (cascade: Order 저장 시 OrderItem도 함께 저장)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(Long userId, Long totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING; // 주문 생성 시 기본 상태
    }

    // 연관 관계 편의 메서드 - OrderItem에 Order를 세팅하고 리스트에 추가
    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    // 주문 상태 Enum
    public enum OrderStatus { PENDING, COMPLETED, CANCELLED }
}