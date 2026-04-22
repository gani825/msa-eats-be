package com.green.eats.order.application.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/*
 주문 생성 요청 DTO
 totalAmount: 클라이언트 계산 총액 → 서버에서 재계산 후 비교 (금액 위변조 방지)
 */
@Data
public class OrderPostReq {

    // 주문 항목 리스트 - 최소 하나 이상 있어야 함
    @NotEmpty(message = "주문 항목이 최소 하나는 있어야 합니다.")
    private List<OrderPostItemReq> items;

    // 클라이언트가 계산한 총 결제 금액 (서버 검증용)
    @Positive(message = "0 이상이어야 합니다.")
    private Integer totalAmount;
}