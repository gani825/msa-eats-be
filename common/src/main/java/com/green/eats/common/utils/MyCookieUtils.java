package com.green.eats.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 빈등록
public class MyCookieUtils {

    // (보안)쿠키에 데이터를 담아서 Client한테 명령
    public void setCookie(HttpServletResponse res, String key, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge); // 쿠키 내용이 살아있는 시간(초단위)
        cookie.setHttpOnly(true); // 보안 쿠키 활성화 → 브라우저의 JS가 접근 불가 (XSS 방어)
        // path 설정을 하면 그 URL일 때만 해당 쿠키가 서버쪽으로 넘어온다.
        // path 설정을 하지 않으면 모든 요청마다 해당 쿠키가 서버쪽으로 넘어온다.
        if (path != null) {
            cookie.setPath(path);
        }
        res.addCookie(cookie);
    }

    // 특정 key의 쿠키 value 반환 (없으면 null)
    public String getValue(HttpServletRequest req, String key) {
        Cookie cookie = getCookie(req, key);
        return cookie == null ? null : cookie.getValue();
    }

    // 특정 key의 Cookie 객체 반환
    public Cookie getCookie(HttpServletRequest req, String key) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) { // 쿠키에 뭔가 담겨져 있다면
            for (int i = 0; i < cookies.length; i++) {
                Cookie c = cookies[i];
                if (c.getName().equals(key)) { // key 이름으로 담겨진 쿠키가 있니?
                    return c;
                }
            }
        }
        return null;
    }

    // 쿠키 삭제: maxAge=0으로 세팅하면 브라우저가 즉시 삭제
    public void deleteCookie(HttpServletResponse res, String key, String path) {
        setCookie(res, key, null, 0, path);
    }
}