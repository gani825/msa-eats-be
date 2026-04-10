package com.green.eats.gateway.security;

import com.green.eats.gateway.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @Configuration 애노테이션 아래에 있는 @Bean은 무조건 싱글톤이다.
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패 시 401 응답 처리

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // JWT 방식은 세션을 안 쓰기 때문에 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(hb -> hb.disable()) // 브라우저 기본 로그인 팝업 비활성화
                .formLogin(fl -> fl.disable()) // 시큐리티 기본 로그인 폼 비활성화
                .logout(logout -> logout.disable())
                // JWT 방식은 쿠키 세션을 안 써서 CSRF 공격이 성립하지 않으므로 비활성화
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // /api/order/** 는 로그인한 사용자만 접근 가능
                        .requestMatchers("/api/order/**").authenticated()
                        // 나머지 모든 요청은 인증 없이 허용
                        .anyRequest().permitAll()
                )
                // 인증 실패 시 (401) 처리 → JwtAuthenticationEntryPoint 에서 응답
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // UsernamePasswordAuthenticationFilter 앞에 JWT 필터 삽입
                // 즉 요청이 들어오면 JWT 검증을 먼저 하고 그 다음 시큐리티 필터 체인이 실행됨
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        config.setAllowCredentials(true); // 쿠키 포함 요청 허용
        config.setMaxAge(3600L); // preflight 요청 결과를 1시간 캐싱

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 CORS 설정 적용
        return source;
    }
}