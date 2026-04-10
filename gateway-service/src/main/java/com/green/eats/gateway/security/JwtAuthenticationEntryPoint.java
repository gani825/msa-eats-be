package com.green.eats.gateway.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

// 시큐리티 필터에서 발생한 예외는 Spring의 GlobalExceptionHandler가 잡지 못한다.
// 시큐리티는 DispatcherServlet 앞단에서 동작하기 때문이다.
// 이 클래스가 시큐리티 예외를 받아서 HandlerExceptionResolver를 통해 GlobalExceptionHandler로 전달하는 브릿지 역할을 한다.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    // HandlerExceptionResolver 타입 빈이 여러 개라 @Qualifier로 특정 빈을 지정
    // "handlerExceptionResolver" = Spring MVC가 기본으로 등록하는 복합 resolver
    // 이 resolver가 내부적으로 GlobalExceptionHandler에 예외를 위임한다
    public JwtAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    // 인증이 필요한 요청인데 인증 정보가 없을 때 Spring Security가 이 메서드를 호출한다
    // TokenAuthenticationFilter에서 request.setAttribute("exception", e)로 저장한 예외를
    // 꺼내서 GlobalExceptionHandler로 전달 → 401 응답 반환
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        resolver.resolveException(request, response, null, (Exception) request.getAttribute("exception"));
    }
}