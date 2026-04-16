package com.green.eats.order.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @Tsid
    private Long id;

    // 지연 로딩 - 실제 Order 데이터가 필요할 때만 쿼리 실행
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long menuId; // 주문한 메뉴 ID
    private Integer quantity; // 수량
    private Long price; // 단가

    @Builder
    public OrderItem(Long menuId, Integer quantity, Long price) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
    }

    // Order 연관 관계 편의 메서드에서 호출됨
    public void setOrder(Order order) { this.order = order; }
}