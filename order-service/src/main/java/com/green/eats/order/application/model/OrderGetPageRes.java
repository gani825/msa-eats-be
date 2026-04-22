package com.green.eats.order.application.model;

import lombok.*;

import java.util.List;

// 커서 기반 페이지네이션 응답 DTO
@Getter
@AllArgsConstructor
public class OrderGetPageRes {
    private List<OrderDto> orders;
    private boolean hasNext;
    private Long nextLastId;
}