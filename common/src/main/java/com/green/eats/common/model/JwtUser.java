package com.green.eats.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

// JWT를 만들 때 payload에 담을 객체
// 최소한의 정보만 담는 것이 보안상 좋음
@Getter
@AllArgsConstructor
public class JwtUser {
    private long signedUserId; // 로그인한 유저의 PK
}