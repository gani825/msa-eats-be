package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void signup(UserSignupReq req) {
        // 회원가입 시켜주세요.
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setPassword(req.getPassword());
        newUser.setName(req.getName());
        newUser.setAddress(req.getAddress());

        userRepository.save(newUser); // INSERT 쿼리 실행

        UserEvent userEvent = UserEvent.builder()
                .userId( newUser.getId() )
                .name( newUser.getName() )
                .eventType( UserEventType.CREATE )
                .build();

        kafkaTemplate.send("user-topic", String.valueOf(newUser.getId()), userEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        // 성공 시 로그
                        log.info("✅ [Kafka Success] Topic: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    } else {
                        // 실패 시 로그
                        log.error("❌ [Kafka Failure] 원인: {}", ex.getMessage());
                    }
                });
    }

}

    // 기존에서 추가된 부분

    public User signin(UserSignupReq req) {
        // 이메일로 유저 조회
        User signedUser = userRepository.findByEmail(req.getEmail());
        log.info("signedUser: {}", signedUser);

        // 유저가 없거나 비밀번호 불일치 시 예외
        // passwordEncoder.matches(평문, 암호화된값) → 일치 여부 확인
        if (signedUser == null || !passwordEncoder.matches(req.getPassword(), signedUser.getPassword())) {
            notFoundUser();
        }
        return signedUser;
    }

    // 예외를 별도 메서드로 분리 → 가독성 향상
    private void notFoundUser() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디, 비밀번호를 확인해주세요.");
    }
}
