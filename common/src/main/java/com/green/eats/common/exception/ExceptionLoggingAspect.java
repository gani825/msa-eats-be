package com.green.eats.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/*
 예외 자동 로깅 AOP (Aspect-Oriented Programming)

 @AfterThrowing: 대상 메서드에서 예외가 던져진 직후 실행
 → 예외를 잡는 게 아니라 예외가 발생했을 때 끼어들어서 로그만 남기고 예외는 그대로 전파
 */
@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    // 포인트컷: application 패키지 하위만 적용
    // constants, entity 등 record/final 클래스는 CGLIB 프록시 불가 → 제외
    @AfterThrowing(pointcut = "execution(* com.green.eats..application..*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("[Exception] {}.{} - {}",
                joinPoint.getSignature().getDeclaringTypeName(), // 클래스명
                joinPoint.getSignature().getName(), // 메서드명
                ex.getMessage()); // 에러 메시지
    }
}