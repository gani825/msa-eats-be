package com.green.eats.common.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

/*
 공통 에러 코드
 - 각 서비스의 ErrorCode와 구분하기 위해 common 모듈에 분리
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    NO_EXISTED_USER("c001", "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    INVALID_TOKEN("c002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String code;  // 에러 식별 코드
    private final String message; // 클라이언트 응답 메시지
    private final HttpStatus httpStatus; // HTTP 상태 코드
}