package com.green.eats.common.enumcode;

//프론트엔드에 Enum 정보를 전달하기 위한 Record
// record: 불변 데이터 객체 (Getter, equals, hashCode, toString 자동 생성)
public record EnumMapperValue(String code, String value) {
    public EnumMapperValue(EnumMapperType enumMapperType) {
        this(enumMapperType.getCode(), enumMapperType.getValue());
    }
}
