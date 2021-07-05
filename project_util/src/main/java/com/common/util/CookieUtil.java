package com.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    
    public final static Integer COOKIE_VALIDATION_SECOND =  60 * 60 ;

    public static Cookie createCookie(String cookieName, String value) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_VALIDATION_SECOND);
        cookie.setPath("/");
        return cookie;
    }
    
    public static Cookie addCookie(HttpServletResponse response, String cookieName, String value) {
        Cookie cookie = createCookie(cookieName, value);
        response.addCookie(cookie);
        return cookie;
    }
    
    public static Cookie addCookie(String cookieName, String value) {
        return addCookie(SessionUtil.getResponse(), cookieName, value);
    }

    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }
    
    public static Cookie getCookie(String cookieName) {
        return getCookie(SessionUtil.getRequest(), cookieName);
    }
    
}