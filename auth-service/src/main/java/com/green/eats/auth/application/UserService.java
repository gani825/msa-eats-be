package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.common.constants.UserEventType;
import com.green.eats.common.model.UserEvent;
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

        // 회원가입 유저 객체 생성 후 저장
        User newUser = new User();
        newUser.setEmail( req.getEmail() );
        newUser.setPassword( hashedPassword );
        newUser.setName( req.getName() );
        newUser.setAddress( req.getAddress() );
        newUser.setEnumUserRole( req.getUserRole() ); // 역할 설정

        userRepository.save(newUser);

        // 회원가입 이벤트 생성 후 Kafka에 전송
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

    // 예외를 별도 메서드로 분리 → 가독성 향상
    private void notFoundUser() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디, 비밀번호를 확인해 주세요.");
    }
}