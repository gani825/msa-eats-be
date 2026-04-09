package com.green.eats.auth.application.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSinginReq {
    @Email // 이메일 형식 검증
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}