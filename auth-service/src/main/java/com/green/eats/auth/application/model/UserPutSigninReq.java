package com.green.eats.auth.application.model;

import lombok.Data;

@Data
public class UserPutSigninReq {
    private String address;
    private String name;
}
