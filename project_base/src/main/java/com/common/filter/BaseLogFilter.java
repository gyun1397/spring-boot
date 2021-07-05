package com.common.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;
import com.common.util.BeanUtil;
import com.common.util.HttpUtil;
import com.common.util.ObjectUtil;
import com.common.util.StringUtil;
import com.common.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class BaseLogFilter implements Filter {
    private static final Set<String> ALLOWED_PATHS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList("","/api", "/api/login", "/api/logout", "/batch/internal/**", "/api/batch/**", "/api/versions/**", "/error", "/error/**", "/**/scheme", "/**/schema")));

    private final MultipartResolver multipartResolver;
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // ResponseWrapper response = new ResponseWrapper((HttpServletResponse) res);
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        // boolean allowedPath = ALLOWED_PATHS.contains(path);
        boolean allowedPath = StringUtil.urlPatternCheck(path, ALLOWED_PATHS);
        if (allowedPath) {
            chain.doFilter(req, res);
        } else {
            String jwt = TokenUtil.getToken();
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            if (HttpUtil.isMultipartForm(request)) {
                chain.doFilter(request, responseWrapper);
                logging(request, responseWrapper.getStatus(), HttpUtil.getBody(responseWrapper), jwt);
            } else {
                CustomRequestBodyWrapper requestWrapper = new CustomRequestBodyWrapper(request);
                chain.doFilter(requestWrapper, responseWrapper);
                logging(requestWrapper, responseWrapper.getStatus(), HttpUtil.getBody(responseWrapper), jwt);
                
            }
            responseWrapper.copyBodyToResponse();
        }
    }
    
    private void logging(HttpServletRequest req, Integer responseState, String responseBody, String jwt) {
        String userId = TokenUtil.getUsername(jwt);
        String clientIp = HttpUtil.getClientIp(req);
        String queryStringUrl = req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");
        String params = HttpUtil.getParameterQueryString(req);
        String body = getRequestBody(req);
        log.debug("[{}]{} [API Log] User ID : {}, Token : {}, Body : {}, Params: {}, Response-{}: {}", clientIp, queryStringUrl, userId, jwt, body, params, responseState, responseBody);
        return;
    }
    
    private String getRequestBody(HttpServletRequest request) {
        String requestMethod = request.getMethod().toUpperCase();
        String requestBody = "";
        if (StringUtil.in(requestMethod, "POST", "PATCH", "PUT")) {
            if (HttpUtil.isMultipartForm(request)) {
                requestBody = getMultipartBody(request);
            } else {
                requestBody = HttpUtil.getBody(request);
            }
        }
        return requestBody;
    }
    
    private String getMultipartBody(HttpServletRequest request) {
        String multipartBody = "";
        MultipartHttpServletRequest multipartHttpRequest = multipartResolver.resolveMultipart(request);
        MultiValueMap<String, MultipartFile> files = multipartHttpRequest.getMultiFileMap();
        
        Map<String, List<String>> result = new HashMap<>();
        files.entrySet().stream().forEach(entry -> result.put(entry.getKey(), entry.getValue().stream().map(file -> file.getOriginalFilename()).collect(Collectors.toList())));
        try {
            multipartBody = ObjectUtil.writeValueAsString(result);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return multipartBody;
    }

    public class CustomRequestBodyWrapper extends HttpServletRequestWrapper {
        private byte[] bytes       = null;
        private String requestBody = null;

        public CustomRequestBodyWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.bytes = IOUtils.toByteArray(super.getInputStream());
            this.requestBody = new String(this.bytes);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream bis = new ByteArrayInputStream(this.bytes);
            return new FilterServletInputStream(bis);
        }

        public String getRequestBody() {
            return this.requestBody;
        }

        class FilterServletInputStream extends ServletInputStream {
            private InputStream is;

            public FilterServletInputStream(InputStream bis) {
                this.is = bis;
            }

            @Override
            public int read() throws IOException {
                return this.is.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return this.is.read(b);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        }
    }
}