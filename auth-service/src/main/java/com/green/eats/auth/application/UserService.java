package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupReq req) {
        // 회원가입 시켜주세요.
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setPassword(req.getPassword());
        newUser.setName(req.getName());
        newUser.setAddress(req.getAddress());

        userRepository.save(newUser); // INSERT 쿼리 실행
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
