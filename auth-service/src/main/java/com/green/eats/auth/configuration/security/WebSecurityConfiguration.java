package com.green.eats.auth.application.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration 애노테이션 아래에 있는 @Bean은 무조건 싱글톤이다.
@Configuration  // 빈등록
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    @Bean  // 메소드 호출로 리턴값 객체를 빈등록하게 된다.
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // 시큐리티에서 session 사용 않겠다. (JWT는 서버에 세션 저장 안 함)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 시큐리티에서 제공해주는 로그인 화면이 있는데 사용하지 않겠다
                .httpBasic(hb -> hb.disable())
                // 어차피 BE가 화면을 만들지 않기 때문에 formLogin 기능도 비활성화하겠다.
                .formLogin(fl -> fl.disable())
                // 어차피 BE가 화면을 만들지 않으면 csrf 공격이 의미가 없기 때문에 비활성화하겠다.
                .csrf(csrf -> csrf.disable())
                // 모든 요청 허용 (auth-service는 로그인/회원가입만 담당하므로 전부 열어둠)
                .authorizeHttpRequests(req -> req.anyRequest().permitAll())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 현존 최강의 단방향 암호화. 시큐리티에 기본 내장되어 있음.
        return new BCryptPasswordEncoder();
    }
}