package com.green.eats.auth.application.model;

import lombok.Data;

// 회원가입 요청 시 프론트에서 받는 데이터
@Data // @Getter + @Setter + @ToString + @EqualsAndHashCode 한번에 처리
public class UserSignupReq {
    private String email;
    private String password;
    private String name;
    private String address; // 선택값
}