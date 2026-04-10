package com.green.eats.gateway.filter;

import com.green.eats.common.model.JwtUser;
import com.green.eats.common.model.UserPrincipal;
import com.green.eats.common.security.JwtTokenManager;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

// 요청당 한 번만 실행되는 JWT 인증 필터
// 쿠키에서 AT를 꺼내 유효하면 시큐리티 인증 처리 + 커스텀 헤더 추가
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("req-uri: {}", request.getRequestURI()); // 요청 URI 로그 출력

        // 쿠키에 AT가 없으면 null, 있으면 Authentication 객체 반환
        Authentication authentication = jwtTokenManager.getAuthentication(request);
        log.info("authentication: {}", authentication);

        // 기본값은 원본 request, 인증 성공 시 헤더 추가 래퍼로 교체
        HttpServletRequest requestToUse = request;

        try {
            if (authentication != null) { // 로그인 상태 (AT 유효)

                // 시큐리티 컨텍스트에 인증 정보 등록 → 인증 완료 처리
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 인증 정보가 있는 경우 헤더를 축다하기 위해 요청을 감싼다.
                if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
                    JwtUser jwtUser = userPrincipal.getJwtUser();
                    log.info("============= jwtUser: {}", jwtUser);
                    // 한글 이름은 HTTP 헤더에 담을 수 없으므로 URL 인코딩
                    String encodedUserName = URLEncoder.encode(jwtUser.getName(), StandardCharsets.UTF_8);

                    // request는 불변이라 헤더를 직접 추가할 수 없음
                    // → Wrapper 패턴으로 X-User-Id, X-User-Name 헤더를 가상으로 추가
                    requestToUse = new HttpServletRequestWrapper(request) {

                        // 헤더 이름으로 단일 값 조회
                        @Override
                        public String getHeader(String name) {
                            if ("X-User-Id".equals(name)) return String.valueOf(jwtUser.getSignedUserId());
                            if ("X-User-Name".equals(name)) return encodedUserName;
                            return super.getHeader(name);
                        }

                        // 전체 헤더 이름 목록 조회 시 커스텀 헤더도 포함
                        @Override
                        public Enumeration<String> getHeaderNames() {
                            List<String> names = Collections.list(super.getHeaderNames());
                            if (!names.contains("X-User-Id")) names.add("X-User-Id");
                            if (!names.contains("X-User-Name")) names.add("X-User-Name");
                            return Collections.enumeration(names);
                        }

                        // 헤더 이름으로 값 목록 조회
                        @Override
                        public Enumeration<String> getHeaders(String name) {
                            if ("X-User-Id".equals(name)) {
                                return Collections.enumeration(Collections.singletonList(String.valueOf(jwtUser.getSignedUserId())));
                            }
                            if ("X-User-Name".equals(name)) {
                                return Collections.enumeration(Collections.singletonList(encodedUserName));
                            }
                            return super.getHeaders(name);
                        }
                    };
                }

            } else { // 비로그인 상태 (AT 없음 or 유효하지 않음)
                // 이후 필터나 핸들러에서 꺼내 쓸 수 있도록 예외를 속성에 저장
                request.setAttribute("exception", new MalformedJwtException("토큰 확인"));
            }

        } catch (Exception e) {
            // 토큰 파싱 중 예외 발생 (만료, 변조 등) → 속성에 저장 후 체인 계속 진행
            request.setAttribute("exception", e);
        }

        // 다음 필터에 request(원본 or 래퍼), response 전달
        filterChain.doFilter(requestToUse, response);
    }
}