package com.green.eats.store.application.model;

import com.green.eats.store.enumcode.EnumMenuCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

// 메뉴 등록 요청 시 프론트에서 받는 데이터
@Getter
@ToString
public class MenuPostReq {

    @NotBlank // null, 빈 문자열, 공백 허용 안 함
    private String name; // 메뉴 이름

    @Positive // 0보다 큰 양수만 허용
    private Integer price; // 메뉴 가격

    private Integer stockQuantity; // 재고 수량

    @NotNull // null 허용 안 함
    private EnumMenuCategory menuCategory; // 메뉴 카테고리 (Enum 타입)
}