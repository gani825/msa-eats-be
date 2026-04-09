package com.green.eats.common.auth;

import com.green.eats.common.model.UserDto;

public class UserContext {
    // ThreadLocal: 스레드(요청)별로 독립된 저장공간
    // 요청 A와 요청 B가 동시에 들어와도 서로 데이터 섞이지 않음
    private static final ThreadLocal<UserDto> USER_HOLDER = new ThreadLocal<>();

    public static void set(UserDto user) { USER_HOLDER.set(user); } // 저장
    public static UserDto get() { return USER_HOLDER.get(); }  // 조회
    public static void clear() { USER_HOLDER.remove(); }  // 삭제
}