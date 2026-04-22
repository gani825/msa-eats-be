package com.green.eats.common.model;

import lombok.*;

/**
 order-service에서 store-service의 메뉴 정보를 FeignClient로 조회할 때 사용하는 응답 DTO
 - 주문 상세 조회 시 menuId → 메뉴명 매핑을 위해 사용됨
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGetClientRes {
    private Long menuId;
    private String name;
    private Integer price;
}