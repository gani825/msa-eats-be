package com.green.eats.order.application.model;

import com.green.eats.order.enumcode.EnumOrderStatus;
import lombok.*;

/*
 주문 목록 조회 결과 DTO
 - JPQL에서 new 연산자로 직접 생성하므로 @AllArgsConstructor 필수
 */
@Getter
@AllArgsConstructor
public class OrderDto {
    private Long orderId;
    private Integer totalAmount;
    private EnumOrderStatus status;
    private String userName; // UserCache에서 가져오는 사용자 이름
}