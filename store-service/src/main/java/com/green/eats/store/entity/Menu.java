package com.green.eats.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.eats.store.enumcode.EnumMenuCategory;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity // DB 테이블과 매핑되는 JPA 엔티티
@Getter
@Setter
public class Menu {

    @Id
    @Tsid // 시간 순 정렬 가능한 Long 타입 ID 자동 생성
    private Long id;

    @Column(nullable = false, length = 255)
    private String name; // 메뉴 이름

    @Column(nullable = false)
    private Integer price; // 메뉴 가격

    @Column(nullable = false)
    private Integer stockQuantity; // 재고 수량

    // DB 컬럼명: cd_category, DB에는 코드("01")로 저장
    @Column(nullable = false, name = "cd_category")
    @JsonProperty("menuCategory")
    private EnumMenuCategory enumMenuCategory;

    // 주문 시 재고 차감 메서드
    // 재고가 부족하면 RuntimeException 발생
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new RuntimeException("상품 '" + name + "'의 재고가 부족합니다. (현재: " + stockQuantity + ")");
        }
        this.stockQuantity = restStock;
    }
}