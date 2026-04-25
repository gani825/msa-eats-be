package com.green.eats.order.outbox;

import com.green.eats.order.enumcode.EnumOutboxStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    @Id @Tsid
    private Long id;

    private Long orderId; // 연관된 주문 ID
    private String topic; // 발행할 Kafka 토픽명
    private String payload; // 발행할 JSON 메시지

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EnumOutboxStatus status; // PENDING(발행대기) → PUBLISHED(발행완료)

    @Builder
    public Outbox(Long orderId, String topic, String payload) {
        this.orderId = orderId;
        this.topic = topic;
        this.payload = payload;
        this.status = EnumOutboxStatus.PENDING; // 생성 시 항상 PENDING
    }

    // 스케줄러가 Kafka 발행 완료 후 호출
    public void markPublished() {
        this.status = EnumOutboxStatus.PUBLISHED;
    }
}