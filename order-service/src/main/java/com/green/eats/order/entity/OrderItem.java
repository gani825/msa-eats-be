package com.green.eats.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Order ↔ OrderItem 순환 참조 방지 - JSON 직렬화 시 order 필드 제외
    @JsonIgnore
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