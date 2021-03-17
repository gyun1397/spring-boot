package com.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j;

/**
 * session Util - Spring에서 제공하는 RequestContextHolder 를 이용하여 request 객체를
 * service까지 전달하지 않고 사용할 수 있게 해줌
 *
 */ 
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class SessionUtil {

    /**
     * attribute 값을 가져 오기 위한 method
     *
     * @param String
     *            attribute key name
     * @return Object attribute obj
     */
    public static Object getAttribute(String key) throws Exception {
        RequestAttributes reqAttrs = RequestContextHolder.getRequestAttributes();
        return reqAttrs == null ? null : (Object) reqAttrs.getAttribute(key, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static String getString(String key) throws Exception {
        Object obj = getAttribute(key);
        return obj == null ? null : (String) obj;
    }

    public static Integer getInt(String key) throws Exception {
        Object obj = getAttribute(key);
        return obj == null ? null : (Integer) obj;
    }

    public static Boolean getBoolean(String key) throws Exception {
        Object obj = getAttribute(key);
        return obj == null ? null : (Boolean) obj;
    }

    public static <T> T get(String key, Class<T> type) throws Exception {
        Object obj = getAttribute(key);
        return obj == null ? null : (T) obj;
    }

    /**
     * attribute 설정 method
     *
     * @param String
     *            attribute key name
     * @param Object
     *            attribute obj
     * @return void
     */
    public static void setAttribute(String name, Object object) throws Exception {
        if (RequestContextHolder.getRequestAttributes() != null)
            RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 설정한 attribute 삭제
     *
     * @param String
     *            attribute key name
     * @return void
     */
    public static void removeAttribute(String name) throws Exception {
        if (RequestContextHolder.getRequestAttributes() != null)
            RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * session id
     *
     * @param void
     * @return String SessionId 값
     */
    public static String getSessionId() throws Exception {
        return RequestContextHolder.getRequestAttributes() == null ? null
                : RequestContextHolder.getRequestAttributes().getSessionId();
    }

    /**
     * 현재 접속중인 request의 session 객체 return
     * 
     * @return
     */
    public static HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attr != null && attr.getRequest() != null) {
            return attr.getRequest().getSession(true);
        } else {
            return null;
        }
    }

    /**
     * 현재 접속중인 request 객체 return
     * 
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr == null ? null : attr.getRequest();
    }

    public static String getSessionInfo() throws Exception {
        HttpSession session = getSession();
        Map tmpMap = new HashMap();
        Enumeration enumer = session.getAttributeNames();
        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            tmpMap.put(key, session.getAttribute(key));
            log.debug(key + " : " + session.getAttribute(key));
        }
        return "";// ObjectUtil.writeValueAsStringPretty(tmpMap);
    }

    public static String getSessionKeys() {
        try {
            HttpSession session = getSession();
            Map tmpMap = new HashMap();
            List keyList = new ArrayList<String>();
            Enumeration enumer = session.getAttributeNames();
            while (enumer.hasMoreElements()) {
                keyList.add((String) enumer.nextElement());
            }
            tmpMap.put("sessionKeys", keyList);
            return ObjectUtil.writeValueAsStringPretty(tmpMap);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    public static String getApplicationInfo() throws Exception {
        HttpSession session = getSession();
        ServletContext servletContext = session.getServletContext();
        Map tmpMap = new HashMap();
        Enumeration enumer = servletContext.getAttributeNames();
        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            tmpMap.put(key, servletContext.getAttribute(key));
            log.debug(key + " : " + servletContext.getAttribute(key));
        }
        return ObjectUtil.writeValueAsStringPretty(tmpMap);
    }

    public static String getApplicationKeys() {
        try {
            HttpSession session = getSession();
            ServletContext servletContext = session.getServletContext();
            Map tmpMap = new HashMap();
            List keyList = new ArrayList<String>();
            Enumeration enumer = servletContext.getAttributeNames();
            while (enumer.hasMoreElements()) {
                keyList.add((String) enumer.nextElement());
            }
            tmpMap.put("applicationKeys", keyList);
            return ObjectUtil.writeValueAsStringPretty(tmpMap);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    public static boolean isWebRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    public static ServletContext getServletContext() {
        
        HttpSession session = getSession();
        if(session==null) {
            return null;
        }
        return session.getServletContext();
    }
}