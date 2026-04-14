package com.green.eats.common.model;

import com.green.eats.common.enumcode.AbstractEnumCodeConverter;
import com.green.eats.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumUserRole implements EnumMapperType {
    USER("01", "일반유저"),
    ADMIN("02", "관리자"),
    MANAGER("03", "중간관리자") // 중간관리자 역할 추가
    ;

    private final String code;  // DB 저장값
    private final String value; // JSON 응답값

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumUserRole> {
        public CodeConverter() { super(EnumUserRole.class, false); }
    }
}