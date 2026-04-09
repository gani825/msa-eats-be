package com.green.eats.common.auth;

import com.green.eats.common.model.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

// 각 서비스에 로그인 정보를 전달하는 역할
// Gateway가 JWT 검증 후 X-User-Id, X-User-Name 헤더에 담아 전달 → 여기서 꺼내 저장
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Gateway에서 넘겨준 헤더 값 꺼내기
        String userId = request.getHeader("X-User-Id");
        String userName = request.getHeader("X-User-Name");

        if (userId != null && userName != null) {
            try {
                // 이름에 한글이 있을 경우 URL 인코딩되어 있으므로 디코딩
                String decodedUserName = URLDecoder.decode(userName, StandardCharsets.UTF_8);
                UserContext.set(new UserDto(Long.parseLong(userId), decodedUserName));
            } catch (Exception e) {
                // 디코딩 실패 시 원본 사용 (또는 무시)
                UserContext.set(new UserDto(Long.parseLong(userId), userName));
            }
        }
        return true; // true = 다음 로직(Controller) 진행
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 요청 처리 완료 후 반드시 삭제 → 메모리 누수 방지
        UserContext.clear();
    }
}