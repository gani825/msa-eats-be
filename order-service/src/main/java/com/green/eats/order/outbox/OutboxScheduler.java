package com.green.eats.order.outbox;

import com.green.eats.order.enumcode.EnumOutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 5초마다 PENDING 상태 이벤트 조회 후 Kafka 발행
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<Outbox> pendingList = outboxRepository.findAllByStatus(EnumOutboxStatus.PENDING);

        for (Outbox outbox : pendingList) {
            try {
                // Kafka로 메시지 발행
                kafkaTemplate.send(outbox.getTopic(), outbox.getPayload());
                // 발행 성공 시 PUBLISHED로 변경
                outbox.markPublished();
                log.info("[Outbox] 발행 완료 - orderId: {}, topic: {}", outbox.getOrderId(), outbox.getTopic());
            } catch (Exception e) {
                // 발행 실패 시 PENDING 유지 → 다음 스케줄에서 재시도
                log.error("[Outbox] 발행 실패 - orderId: {}, error: {}", outbox.getOrderId(), e.getMessage());
            }
        }
    }
}