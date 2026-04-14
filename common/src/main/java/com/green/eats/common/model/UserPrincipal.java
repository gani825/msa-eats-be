package com.green.eats.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Spring Security가 인증 처리를 할 때 사용하는 객체
// SecurityContextHolder에 이 객체가 담기면 인증됨 상태가 된다
@RequiredArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {
    private final JwtUser jwtUser; // JWT에서 꺼낸 유저 정보

    // 컨트롤러에서 로그인한 유저 ID를 꺼낼 때 사용
    public long getSignedUserId() {
        return jwtUser.getSignedUserId();
    }

    // 인가 체크 - JWT에 담긴 role로 ROLE_USER, ROLE_ADMIN, ROLE_MANAGER 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = String.format("ROLE_%s", jwtUser.getEnumUserRole().name());
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public @Nullable String getPassword() {
        return ""; // 이미 JWT로 인증했으므로 비밀번호 불필요
    }

    @Override
    public String getUsername() {
        return ""; // 이미 JWT로 인증했으므로 username 불필요
    }
}