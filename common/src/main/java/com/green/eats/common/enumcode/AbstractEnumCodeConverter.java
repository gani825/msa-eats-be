package com.green.eats.common.enumcode;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

/*
 JPA Entity 필드 ↔ DB 컬럼 자동 변환을 담당하는 추상 클래스
    흐름
    저장 시: EnumMenuCategory.KOREAN_FOOD → "01" (DB)
    조회 시: "01" (DB) → EnumMenuCategory.KOREAN_FOOD (Entity)

    각 Enum마다 이 클래스를 상속받아 static inner class로 Converter를 만들어 사용
    (예: EnumMenuCategory.CodeConverter)
 */
@RequiredArgsConstructor
public abstract class AbstractEnumCodeConverter<E extends Enum<E> & EnumMapperType>
        implements AttributeConverter<E, String> {

    private final Class<E> targetEnumClass; // 변환 대상 Enum 클래스 (예: EnumMenuCategory.class)
    private final boolean nullable; // DB에 null 저장 허용 여부

    /**
     Entity → DB 저장 시 호출
     Enum 값 → code 문자열로 변환
     */
    @Override
    public String convertToDatabaseColumn(E enumItem) {
        // nullable=false인데 null이 들어왔다면 예외
        if (!nullable && enumItem == null) {
            throw new IllegalArgumentException(
                    String.format("%s(는)은 NULL로 저장할 수 없습니다.", targetEnumClass.getSimpleName()));
        }
        return EnumConvertUtils.toCode(enumItem); // Enum → code 문자열
    }

    /*
     DB 조회 → Entity 변환 시 호출
     code 문자열 → Enum 값으로 변환
     */
    @Override
    public E convertToEntityAttribute(String dbData) {
        // nullable=false인데 DB 값이 비어있다면 예외
        if (!nullable && StringUtils.isBlank(dbData) || dbData == null) {
            throw new IllegalArgumentException(
                    String.format("%s(는)가 DB에 NULL 혹은 Empty로 저장되어 있습니다.", targetEnumClass.getSimpleName()));
        }
        return EnumConvertUtils.ofCode(targetEnumClass, dbData); // code 문자열 → Enum
    }
}