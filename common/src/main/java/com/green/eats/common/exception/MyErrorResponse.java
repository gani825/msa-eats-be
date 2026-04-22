package com.green.eats.common.exception;

import lombok.*;

/*
 에러 응답 DTO
 - ServiceExceptionHandler에서 예외 발생 시 이 형태로 클라이언트에 반환
 */
@Getter
@Builder
@AllArgsConstructor
public class MyErrorResponse {
    private final String code;  // 에러 식별 코드 (프론트에서 에러 분기 처리 시 사용)
    private final String message; // 사용자에게 보여줄 에러 메시지
}