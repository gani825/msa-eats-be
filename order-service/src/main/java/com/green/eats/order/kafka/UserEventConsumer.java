package com.green.eats.order.kafka;

import com.green.eats.common.constants.UserEventType;
import com.green.eats.common.model.UserEvent;
import com.green.eats.order.application.UserCacheRepository;
import com.green.eats.order.entity.UserCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final UserCacheRepository userCacheRepository;

    // user-topic을 구독, order-group 기준으로 offset 관리
    @Transactional
    @KafkaListener(topics = "user-topic", groupId = "order-group")
    public void consume(UserEvent event) {
        log.info("📢 Kafka 메시지 수신됨: {}", event);

        try {
            UserEventType type = event.getEventType();

            if (type == UserEventType.CREATE || type == UserEventType.UPDATE) {
                // 동일 userId면 덮어쓰기 (Idempotent 처리)
                UserCache userCache = UserCache.builder()
                        .userId(event.getUserId())
                        .name(event.getName())
                        .build();
                userCacheRepository.save(userCache);
                log.info("✅ UserCache 저장/업데이트 완료: {}", event.getUserId());

            } else if ("DELETE".equals(type)) {
                // 회원 탈퇴 시 캐시 삭제
                userCacheRepository.deleteById(event.getUserId());
                log.info("🗑️ UserCache 삭제 완료: {}", event.getUserId());
            }

        } catch (Exception e) {
            // 예외를 잡지 않으면 Kafka가 메시지를 무한 재처리할 수 있으므로 반드시 catch
            log.error("❌ Kafka 메시지 처리 중 오류 발생: ", e);
        }
    }
}