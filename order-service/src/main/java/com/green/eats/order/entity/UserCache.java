package com.green.eats.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserCache {
    // user-service의 원본 ID를 그대로 PK로 사용 (별도 ID 생성 없이 동기화)
    @Id
    private Long userId;
    private String name;

    @Builder
    public UserCache(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}