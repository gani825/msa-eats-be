package com.green.eats.common;

import com.green.eats.common.constants.ConstJwt;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// com.green.eats.common 패키지 하위를 컴포넌트 스캔 대상에 포함
@ComponentScan("com.green.eats.common")
// application.yaml의 constants.jwt 설정을 ConstJwt로 읽어오기 위한 스캔
@ConfigurationPropertiesScan("com.green.eats.common")
// ConstJwt record를 명시적으로 빈 등록
@EnableConfigurationProperties(ConstJwt.class)
public class CommonConfiguration { }
// → AutoConfiguration.imports에 등록되어 다른 서비스에서도 자동으로 불러와진다.