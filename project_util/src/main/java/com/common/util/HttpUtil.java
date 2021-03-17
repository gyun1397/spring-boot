package com.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil {
    private static final String ISO_8859_1 = "8859_1";
    private static final String UTF_8      = "UTF-8";
    private static InetAddress  inet       = null;

    public static String getServerIp() {
        try {
            if (inet == null) {
                inet = InetAddress.getLocalHost();
            }
            return inet.getHostAddress();
        } catch (UnknownHostException e) {
            log.debug(e.getMessage());
            return "255.255.255.255";
        }
    }

    public static String getClientIp() {
        return getClientIp(SessionUtil.getRequest());
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            request = SessionUtil.getRequest();
            if (request == null) {
                return "Unknown IP(Request 객체가 없음)";
            }
        }
        String clientIp = request.getHeader("X-Forwarded-For");
        if (isEmptyClientIp(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (isEmptyClientIp(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isEmptyClientIp(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isEmptyClientIp(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isEmptyClientIp(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    public static boolean isEmptyClientIp(String clientIp) {
        return StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp);
    }

    /**
     * ServletRequest#getLocalName() returns local hostname.
     * ServletRequest#getLocalAddr() returns local IP. ServletRequest#getLocalPort()
     *
     * @return
     */
    public static int getServerPort() {
        HttpServletRequest request = SessionUtil.getRequest();
        return request.getLocalPort();
    }

    /**
     * parameter 정보를 map 형태로 return 한다.
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameter(HttpServletRequest request) {
        boolean isMultipartForm = isMultipartForm(request);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            Enumeration<String> paramNames = (Enumeration<String>) request.getParameterNames();
            String paramName = null;
            while (paramNames.hasMoreElements()) {
                paramName = paramNames.nextElement();
                String[] values = request.getParameterValues(paramName);
                if (values != null && values.length == 1) {
                    if (isMultipartForm) {
                        paramMap.put(paramName, toUTF8(values[0]));
                    } else {
                        paramMap.put(paramName, values[0]);
                    }
                } else {
                    if (isMultipartForm) {
                        String[] UTF8_values = new String[values.length];
                        for (int i = 0; i < values.length; i++) {
                            UTF8_values[i] = toUTF8(values[i]);
                        }
                        paramMap.put(paramName, UTF8_values);
                    } else {
                        paramMap.put(paramName, values);
                    }
                }
            }
        } catch (Exception e) {
        }
        return paramMap;
    }

    public static boolean isMultipartForm(HttpServletRequest request) {
        return StringUtil.nvl(request.getContentType()).indexOf("multipart/form-data") >= 0;
    }

    /**
     * header 정보를 map 형태로 return 한다.
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getHeader(HttpServletRequest request) {
        Map<String, Object> headerMap = new HashMap<String, Object>();
        try {
            Enumeration<String> headerNames = (Enumeration<String>) request.getHeaderNames();
            String headerName = null;
            while (headerNames.hasMoreElements()) {
                headerName = headerNames.nextElement();
                headerMap.put(headerName, request.getHeader(headerName));
            }
        } catch (Exception e) {
        }
        return headerMap;
    }

    public static String getHeaderJsonStr(HttpServletRequest request) {
        Map<String, Object> headerMap = getHeader(request);
        try {
            return ObjectUtil.writeValueAsStringPretty(headerMap);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    public static String getBody(HttpServletRequest request) {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.debug(ex.getMessage());
                    return null;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }


    /**
     * 인자를 분석하여 Content Type을 반환함.
     *
     * @param targetInfo
     * @return
     */
    public static String getContentType(String targetInfo) {
        try {
            return FileUtil.fileExtensionMap.get(targetInfo.substring(targetInfo.lastIndexOf(".") + 1));
        } catch (Exception e) {
            return "text/plain";
        }
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = StringUtil.nvl(uri.getHost());
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static String getUrlBasedView(HttpServletRequest request) {
        String reqUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return "root" + reqUrl.replaceAll(".do", "");
    }

    private static String toUTF8(String iso8859_1) {
        try {
            return new String(iso8859_1.getBytes(ISO_8859_1), UTF_8);
        } catch (UnsupportedEncodingException e) {
            return iso8859_1;
        }
    }
}
