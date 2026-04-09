package com.green.eats.common.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

// application-auth-prod.yaml의 constants.jwt 값을 자동으로 이 record에 매핑
// record: 불변 데이터 객체 (Java 16+), Getter·생성자 자동 생성
@ConfigurationProperties(prefix = "constants.jwt")
public record ConstJwt(
        String issuer, // 토큰 발행자
        String bearerFormat, // 토큰 타입 (JWT)
        String claimKey, // payload에 담을 key 이름
        String secretKey, // 서명에 사용할 비밀키
        String accessTokenCookieName, // AT 쿠키 이름
        String accessTokenCookiePath,  // AT 쿠키 유효 경로
        int accessTokenCookieValiditySeconds, // AT 쿠키 유효시간(초)
        long accessTokenValidityMilliseconds, // AT 토큰 유효시간(밀리초)
        String refreshTokenCookieName, // RT 쿠키 이름
        String refreshTokenCookiePath, // RT 쿠키 유효 경로 (reissue 경로만)
        int refreshTokenCookieValiditySeconds, // RT 쿠키 유효시간(초)
        long refreshTokenValidityMilliseconds // RT 토큰 유효시간(밀리초)
) {}