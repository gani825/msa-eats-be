package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void signup(UserSignupReq req) {
        // 회원가입 시켜주세요.
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setPassword(req.getPassword());
        newUser.setName(req.getName());
        newUser.setAddress(req.getAddress());

        userRepository.save(newUser); // INSERT 쿼리 실행
    }
}
