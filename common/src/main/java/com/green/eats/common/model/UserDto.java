package com.green.eats.common.model;

// Dto : Data Transfer Object - 레이어 간 데이터 전달 용도의 객체
// Vo  : Value Object
// record: 불변 + Getter + equals/hashCode 자동 생성
public record UserDto(Long id, String name) {
}