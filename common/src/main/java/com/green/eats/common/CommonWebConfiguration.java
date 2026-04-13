package com.green.eats.common;

import com.green.eats.common.auth.UserContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 공통 Web MVC 설정
 두 가지 역할
 1. 모든 RestController에 API prefix 자동 적용 (예: /api)
    -> @GetMapping("/menu")가 자동으로 /api/menu 로 등록됨
 2. 모든 요청에 UserContextInterceptor 적용
    -> Gateway가 헤더로 넘긴 유저 정보를 UserContext에 저장

 @ConditionalOnWebApplication: 웹 환경(SERVLET)에서만 이 설정이 활성화됨
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonWebConfiguration implements WebMvcConfigurer {
    private final UserContextInterceptor userContextInterceptor;
    private final String apiPrefix;

    // @Value: application.yaml의 constants.api.prefix 값 주입, 없으면 "/api" 기본값 사용
    public CommonWebConfiguration(UserContextInterceptor userContextInterceptor,
                                  @Value("${constants.api.prefix:/api}") String apiPrefix) {
        this.userContextInterceptor = userContextInterceptor;
        log.info("============= apiPrefix: {}", apiPrefix);
        this.apiPrefix = apiPrefix;
    }

    // 모든 @RestController에 prefix를 강제로 붙임
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (StringUtils.hasText(apiPrefix)) {
            configurer.addPathPrefix(apiPrefix,
                    HandlerTypePredicate.forAnnotation(RestController.class));
        }
    }

     // 모든 요청에 UserContextInterceptor를 적용 -> 컨트롤러 실행 전에 헤더에서 유저 정보를 꺼내서 ThreadLocal에 저장
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor);
    }
}