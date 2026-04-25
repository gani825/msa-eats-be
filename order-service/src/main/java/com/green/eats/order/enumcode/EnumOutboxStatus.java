package com.green.eats.order.enumcode;

import com.green.eats.common.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumOutboxStatus implements EnumMapperType {
    PENDING("PENDING", "발행대기"), // Kafka 발행 전
    PUBLISHED("PUBLISHED", "발행완료") // Kafka 발행 완료
    ;

    private final String code;
    private final String value;

    @Override
    public String getCode() { return code; }

    @Override
    public String getValue() { return value; }
}