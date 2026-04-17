package com.green.eats.common.enumcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
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
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.green.eats.common") // CommonController 스캔용
public class EnumAutoConfiguration {

    @Bean
    public EnumMapper enumMapper(ApplicationContext applicationContext,
                                 @Value("${constants.enum.scan-package:null}") String scanPackage) { // yaml에 추가 패키지 설정 가능
        EnumMapper enumMapper = new EnumMapper();
        log.info("scanPackage: {}", scanPackage);

        // 1. 스캔할 패키지 리스트 준비
        List<String> scanPackages = new ArrayList<>();

        // 메인 앱(@SpringBootApplication)의 패키지를 기준으로 스캔
        scanPackages.add(getBasePackage(applicationContext));

        // yaml에 constants.enum.scan-package가 설정되어 있으면 추가 스캔
        if (scanPackage != null) {
            scanPackages.add(scanPackage);
        }

        // 2. 각 패키지를 순회하며 EnumMapperType 구현 Enum 자동 탐색
        for (String basePackage : scanPackages) {
            Map<String, List<EnumMapperValue>> scannedCodes = EnumMapperScanner.scan(basePackage);

            // 3. 스캔된 Enum을 EnumMapper에 등록
            scannedCodes.forEach((key, values) -> enumMapper.put(key, values));
        }

        return enumMapper;
    }

    // @SpringBootApplication이 붙은 메인 클래스의 패키지명 반환
    // 예: com.green.eats.store
    private String getBasePackage(ApplicationContext context) {
        return context.getBeansWithAnnotation(SpringBootApplication.class)
                .values().iterator().next().getClass().getPackageName();
    }
}