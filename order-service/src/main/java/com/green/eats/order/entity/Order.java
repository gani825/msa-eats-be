package com.green.eats.order.entity;

import com.green.eats.order.enumcode.EnumOrderStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 'order'는 SQL 예약어이므로 테이블명 명시
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 필요, 외부 직접 생성 방지
public class Order {
    @Id @Tsid
    private Long id;

    // user_cache 테이블의 userId 참조 (FK 제약 없음, Kafka로 동기화된 캐시)
    @Column(nullable = false)
    private Long userId;

    private Integer totalAmount; // 총 결제 금액

    @Column(length = 20, nullable = false) // 2 → 20 으로 변경 (COMPLETED = 9글자)
    @Enumerated(EnumType.STRING) // DB에 문자열로 저장 ("PENDING", "COMPLETED")
    private EnumOrderStatus status;

    // cascade = ALL: Order 저장/삭제 시 OrderItem도 함께 처리
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(Long userId, Integer totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = EnumOrderStatus.COMPLETED; // PENDING → COMPLETED: 즉시 결제 완료 처리
    }

    // 연관 관계 편의 메서드: OrderItem 추가 시 양방향 관계 동시 설정
    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }
}