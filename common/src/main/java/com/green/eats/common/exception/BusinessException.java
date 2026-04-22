package com.green.eats.common.exception;

import lombok.Getter;

/*
 비즈니스 로직 예외 클래스
 - throw new BusinessException(ErrorCode) 형태로 사용
 - throw BusinessException.of(ErrorCode) 정적 팩토리 메서드도 제공
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException에 메시지 전달
        this.errorCode = errorCode;
    }

    // 정적 팩토리 메서드 (throw BusinessException.of(OrderErrorCode.XXX) 형태로 사용)
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }
}