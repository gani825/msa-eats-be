package com.green.eats.common.exception;

import org.springframework.http.HttpStatus;

/*
 모든 ErrorCode enum이 구현해야 하는 인터페이스
 - BusinessException에서 이 타입으로 받아 처리
 */
public interface ErrorCode {
    String getCode();  // 에러 식별 코드

    String getMessage(); // 클라이언트에 반환할 에러 메시지

    HttpStatus getHttpStatus(); // HTTP 상태 코드
}