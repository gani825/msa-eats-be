package com.green.eats.common.security;

import com.green.eats.common.constants.ConstJwt;
import com.green.eats.common.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final ObjectMapper objectMapper; // (내장)Jackson 라이브러리 DI받을 속성
    private final ConstJwt constJwt;
    private final SecretKey secretKey; // 서명에 사용할 키 (yaml의 secretKey를 디코딩해 생성)

    public JwtTokenProvider(ObjectMapper objectMapper, ConstJwt constJwt) {
        this.objectMapper = objectMapper;
        this.constJwt = constJwt;
        // BASE64로 인코딩된 secretKey를 디코딩해서 실제 암호화 키로 변환
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(constJwt.secretKey()));
        log.info("constJwt: {}", this.constJwt);
    }

    public String generateAccessToken(JwtUser jwtUser) {
        return generateToken(jwtUser, constJwt.accessTokenValidityMilliseconds());
    }

    public String generateRefreshToken(JwtUser jwtUser) {
        return generateToken(jwtUser, constJwt.refreshTokenValidityMilliseconds());
    }

    // JWT(문자열)을 만드는 메소드, 암호화된 문자열(데이터, 토큰만료시간)
    public String generateToken(JwtUser jwtUser, long tokenValidityMilliSeconds) {
        Date now = new Date(); // 현재 시각
        return Jwts.builder()
                // header
                .header().type(constJwt.bearerFormat()) // JWT
                .and()
                // payload
                .issuer(constJwt.issuer()) // 발행자
                .issuedAt(now) // 토큰 생성 일시
                .expiration(new Date(now.getTime() + tokenValidityMilliSeconds)) // 만료 일시
                .claim(constJwt.claimKey(), makeClaimByUserToJson(jwtUser))  // JwtUser 객체를 JSON으로 변환해 담기
                // signature (비밀키로 서명)
                .signWith(secretKey)
                .compact();
    }

    // 직렬화 : JwtUser 객체 → JSON 문자열
    public String makeClaimByUserToJson(JwtUser jwtUser) {
        return objectMapper.writeValueAsString(jwtUser);
    }

    // JWT에서 JwtUser 꺼내기
    public JwtUser getJwtUserFromToken(String token) {
        Claims claims = getClaims(token);
        // signedUser 키값으로 담겨있는 JSON 문자열을 꺼내서
        String json = claims.get(constJwt.claimKey(), String.class);
        // JSON 문자열 → JwtUser 객체로 역직렬화
        return objectMapper.readValue(json, JwtUser.class);
    }

    // JWT의 payload에 담겨있는 Claim들을 리턴한다.
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 서명 검증
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}