package com.green.eats.order.application.model;

import lombok.*;

// 주문 상세 응답 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderGetDetailRes {
    private Long id;
    private String name; // 메뉴명 (store-service에서 조회)
    private Integer price; // 단가
    private Integer quantity; // 수량
}