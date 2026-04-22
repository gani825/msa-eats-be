package com.green.eats.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 전역 예외 처리 핸들러
 - BusinessException 발생 시 ErrorCode에 정의된 HTTP 상태 코드와 메시지 반환
 - @RestControllerAdvice: 모든 @RestController에 적용
 */
@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

     // BusinessException 처리
     // - 비즈니스 로직에서 의도적으로 던진 예외만 처리
     // - ErrorCode에 정의된 HTTP 상태 코드와 메시지를 그대로 반환
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MyErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(MyErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}