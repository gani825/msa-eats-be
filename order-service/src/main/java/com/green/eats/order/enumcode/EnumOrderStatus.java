package com.green.eats.order.enumcode;

import com.green.eats.common.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 주문 상태 Enum
 * - Order.java 내부 enum에서 별도 파일로 분리
 * - getCode(): DB 저장 시 사용
 * - getValue(): FE 응답 시 사용 (@JsonValue 적용됨)
 */
@Getter
@RequiredArgsConstructor
public enum EnumOrderStatus implements EnumMapperType {
    PENDING("PENDING", "주문대기"),
    COMPLETED("COMPLETED", "주문완료"),
    CANCELLED("CANCELLED", "주문취소")
    ;

    private final String code;  // DB 저장값
    private final String value; // FE 응답값 (한글)

    @Override
    public String getCode() { return code; }

    @Override
    public String getValue() { return value; }
}