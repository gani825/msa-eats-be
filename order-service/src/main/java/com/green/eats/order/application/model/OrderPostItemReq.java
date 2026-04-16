package com.green.eats.order.application.model;

import lombok.Data;

@Data
public class OrderPostItemReq {
    private Long menuId; // 주문할 메뉴 ID
    private Integer quantity; // 수량
    private Long price; // 단가
}