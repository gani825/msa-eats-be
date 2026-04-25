package com.green.eats.order.outbox;

import com.green.eats.order.enumcode.EnumOutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    // 스케줄러에서 PENDING 상태 이벤트만 조회해서 Kafka로 발행
    List<com.green.eats.order.outbox.Outbox> findAllByStatus(EnumOutboxStatus status);
}