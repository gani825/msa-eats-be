package com.green.eats.common.enumcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.*;

/*
 basePackage 하위에서 EnumMapperType을 구현한 Enum을 자동으로 탐색해서 등록
 직접 put() 안 해도 새 Enum 추가하면 자동으로 /code API에서 사용 가능해짐
 */
@Slf4j
public class EnumMapperScanner {

    public static Map<String, List<EnumMapperValue>> scan(String basePackage) {
        Map<String, List<EnumMapperValue>> factory = new LinkedHashMap<>();
        // EnumMapperType 구현체만 필터링하는 클래스패스 스캐너 생성
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(EnumMapperType.class));

        Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);

        for (BeanDefinition component : components) {
            try {
                Class<?> clazz = Class.forName(component.getBeanClassName());

                // Enum이면서 EnumMapperType을 구현한 클래스만 처리
                if (clazz.isEnum() && EnumMapperType.class.isAssignableFrom(clazz)) {
                    Class<? extends EnumMapperType> enumClass = (Class<? extends EnumMapperType>) clazz;

                    // 클래스명을 API key로 변환: "EnumMenuCategory" → "menuCategory"
                    String key = convertToCamelCase(clazz.getSimpleName());
                    factory.put(key, toEnumValues(enumClass));
                    log.info("Enum Registered: {} as key '{}'", clazz.getSimpleName(), key);
                }
            } catch (ClassNotFoundException e) {
                log.error("Enum scan error", e);
            }
        }
        return factory;
    }

    private static List<EnumMapperValue> toEnumValues(Class<? extends EnumMapperType> e) {
        return Arrays.stream(e.getEnumConstants()).map(EnumMapperValue::new).toList();
    }

    /*
     PascalCase → camelCase 변환, "Enum" 접두사 제거
     "EnumMenuCategory" → "menuCategory"
     "MenuCategory"  → "menuCategory"
     */
    private static String convertToCamelCase(String name) {
        String target = name.startsWith("Enum") ? name.substring(4) : name;
        return Character.toLowerCase(target.charAt(0)) + target.substring(1);
    }
}