package com.green.eats.common.constants;

// 유저 이벤트 타입 Enum - Kafka 메시지 전송 시 이벤트 종류를 구분하는 데 사용
public enum UserEventType {
    CREATE, // 회원가입
    UPDATE, // 정보 수정
    DELETE  // 탈퇴
}