package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserPutSigninReq;
import com.green.eats.auth.application.model.UserSigninRes;
import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.JwtUser;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.common.security.JwtTokenManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    @PostMapping("/signup")
    public ResultResponse<?> signup(@RequestBody UserSignupReq req) {
        userService.signup(req);
        log.info("req: {}", req);
        return ResultResponse.builder()
                .resultMessage("회원가입 성공")
                .resultData(1)
                .build();
    }

    @PostMapping("/signin")
    public ResultResponse<?> signin(HttpServletResponse res, @RequestBody UserSigninReq req) {
        log.info("req: {}", req);

        // 이메일/비밀번호 검증 → 유저 조회
        User signedUser = userService.signin(req);

        // 인증쿠키: AT + RT 발급
        JwtUser jwtUser = new JwtUser(signedUser.getId(), signedUser.getName(),signedUser.getEnumUserRole());
        jwtTokenManager.issue(res, jwtUser); // 쿠키에 AT, RT 담기

        // 응답 DTO 빌드
        UserSigninRes userSigninRes = UserSigninRes.builder()
                .id(signedUser.getId())
                .name(signedUser.getName())
                .build();

        return ResultResponse.builder()
                .resultMessage("로그인 성공")
                .resultData(userSigninRes)
                .build();
    }

    @PutMapping
    public ResultResponse<?> update(@RequestBody UserPutSigninReq req) {
        // signin() 때 issue()로 담아둔 AT 쿠키에서 유저 정보 꺼내기
        UserDto userDto = UserContext.get();
        userService.update(userDto.id(), req);
        return ResultResponse.builder()
                .resultMessage("수정 완료")
                .resultData(1)
                .build();
    }
}
