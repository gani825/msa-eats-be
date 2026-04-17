package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserPutSigninReq;
import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.common.constants.UserEventType;
import com.green.eats.common.model.UserEvent;
import jakarta.transaction.Transactional;
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
    private final KafkaTemplate<String, Object> kafkaTemplate; // Kafka 메시지 전송 템플릿

    public void signup(UserSignupReq req) {
        // 비밀번호 BCrypt 암호화
        String hashedPassword = passwordEncoder.encode(req.getPassword());

        // 회원가입 유저 객체 생성 후 DB 저장
        User newUser = new User();
        newUser.setEmail( req.getEmail() );
        newUser.setPassword( hashedPassword );
        newUser.setName( req.getName() );
        newUser.setAddress( req.getAddress() );
        newUser.setEnumUserRole( req.getUserRole() ); // 역할 설정

        userRepository.save(newUser);

        // 회원가입 이벤트 Kafka에 전송
        sendKafkaEvent(newUser.getId(), newUser.getName(), UserEventType.CREATE);
    }

    public User signin(UserSigninReq req) {
        // 이메일로 유저 조회
        User signedUser = userRepository.findByEmail( req.getEmail() );
        log.info("signedUser: {}", signedUser);

        // 유저가 없거나 비밀번호 불일치 시 예외
        // passwordEncoder.matches(평문, 암호화된값) → 일치 여부 확인
        if(signedUser == null || !passwordEncoder.matches( req.getPassword(), signedUser.getPassword() )) {
            notFoundUser();
        }
        return signedUser;
    }

    @Transactional
    public void update(Long userId, UserPutSigninReq req) {
        // userId로 유저 조회 (없으면 예외)
        User user = userRepository.findById(userId).orElseThrow();

        // 변경된 값 세팅
        user.setName(req.getName());
        user.setAddress(req.getAddress());
        userRepository.save(user);

        // 수정 이벤트 Kafka에 전송
        sendKafkaEvent(user.getId(), user.getName(), UserEventType.UPDATE);
    }

    // Kafka 이벤트 전송 공통 메서드
    private void sendKafkaEvent(Long userId, String name, UserEventType eventType) {
        UserEvent userEvent = UserEvent.builder()
                .userId(userId)
                .name(name)
                .eventType(eventType)
                .build();

        kafkaTemplate.send("user-topic", String.valueOf(userId), userEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ [Kafka Success] Topic: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ [Kafka Failure] 원인: {}", ex.getMessage());
                    }
                });
    }

    // 예외를 별도 메서드로 분리
    private void notFoundUser() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디, 비밀번호를 확인해 주세요.");
    }
}