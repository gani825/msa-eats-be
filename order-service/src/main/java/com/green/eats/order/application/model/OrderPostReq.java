package com.green.eats.order.application.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderPostReq {
    // 주문 항목 리스트 - 최소 하나 이상 있어야 함
    @NotEmpty(message = "주문 항목이 최소 하나는 있어야 합니다.")
    private List<OrderPostItemReq> items;
}