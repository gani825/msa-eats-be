package com.green.eats.common.enumcode;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Enum 코드 목록을 key-value 형태로 관리하는 저장소
//  프론트엔드가 /code?code_type=menuCategory 로 요청하면 해당 Enum의 코드 목록을 응답해줌
public class EnumMapper {
    private Map<String, List<EnumMapperValue>> factory = new LinkedHashMap<>();

    // Enum 클래스를 직접 등록 (수동 등록용)
    public void put(String key, Class<? extends EnumMapperType> e) {
        factory.put(key, toEnumValues(e));
    }
    // 추가: 이미 변환된 리스트를 직접 저장 (스캐너용)
    // 이미 변환된 리스트를 등록 (EnumMapperScanner 자동 등록용)
    public void put(String key, List<EnumMapperValue> values) {
        factory.put(key, values);
    }

    // Enum 클래스의 모든 상수 → EnumMapperValue 리스트로 변환
    private List<EnumMapperValue> toEnumValues(Class<? extends EnumMapperType> e) {
        return Arrays.stream(e.getEnumConstants()) // Array to Stream
                .map(EnumMapperValue::new) // map은 같은 크기의 스트림을 만든다. 메소드 참조 .map(item -> new EnumMapperValue(item)) 이렇게 작성된 것과 같다.
                .toList(); // 최종연산
    }

    // key로 코드 목록 조회 (CommonController에서 사용)
    public List<EnumMapperValue> get(String key) {
        return factory.get(key);
    }
}
