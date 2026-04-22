package com.green.eats.order.exception;

import com.green.eats.common.exception.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    /*
     주문 금액 불일치
     - 클라이언트가 보낸 totalAmount와 서버에서 재계산한 값이 다를 때 발생
     - 금액 위변조 방지 목적
     */
    NOT_MATCHED_ALL_AMOUNT("o001", "최종 결제 금액이 맞지않습니다.", HttpStatus.BAD_REQUEST),
    NAME("o002", "내용", HttpStatus.UNAUTHORIZED)
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}