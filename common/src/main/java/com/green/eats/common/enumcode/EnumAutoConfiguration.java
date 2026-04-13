package com.green.eats.common.enumcode;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/*
 EnumMapper 빈을 자동으로 생성하는 설정 클래스

 동작 흐름
 1. 메인 앱(@SpringBootApplication)의 패키지를 기준으로 EnumMapperScanner 실행
 2. EnumMapperType을 구현한 Enum을 자동 탐색
 3. EnumMapper에 등록 → CommonController에서 /code API로 제공

 @ComponentScan: common 패키지의 CommonController도 빈 등록 대상에 포함
 */
@Configuration
@ComponentScan(basePackages = "com.green.eats.common")
public class EnumAutoConfiguration {

    @Bean
    public EnumMapper enumMapper(ApplicationContext applicationContext) {
        EnumMapper enumMapper = new EnumMapper();

        // 메인 앱 패키지 경로 추출 → 해당 패키지 하위 Enum 자동 스캔
        String basePackage = getBasePackage(applicationContext);
        Map<String, List<EnumMapperValue>> scannedCodes = EnumMapperScanner.scan(basePackage);

        // 스캔된 Enum을 EnumMapper에 등록
        scannedCodes.forEach((key, values) -> enumMapper.put(key, values));

        return enumMapper;
    }

    /*
     @SpringBootApplication이 붙은 메인 클래스의 패키지명 반환
    예: com.green.eats.store
     */
    private String getBasePackage(ApplicationContext context) {
        return context.getBeansWithAnnotation(SpringBootApplication.class)
                .values().iterator().next().getClass().getPackageName();
    }
}