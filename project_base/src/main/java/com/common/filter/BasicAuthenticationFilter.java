package com.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.common.util.StringUtil;
import com.common.util.TokenUtil;

public abstract class BasicAuthenticationFilter  extends OncePerRequestFilter {
    private static Set<String> ALLOWED_PATHS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList("/api", "/api/login", "/api/logout", "/auth/**", "/error/**", "/api/**/scheme", "/api/**/schema")));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        if (!StringUtil.urlPatternCheck(path, ALLOWED_PATHS)) {
            AbstractAuthenticationToken authentication = null;
            try {
                Optional<String> token = TokenUtil.extractJWT(request);
                if (token.isPresent()) {
                    Optional<String> user = TokenUtil.verifyJWT(token.get());
                    if (user.isPresent()) {
                        /*
                         * 필요시 권한 체크 하는 로직 주석 해제
                         * privileges -> API Pattern or Full Resource일 경우 사용
                         */
//                        if (checkPrivilege(path, TokenUtil.getPrivileges(token.get()))) {
//                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "권한 없음");
//                        }
                        request.setAttribute(TokenUtil.ATTRIBUTE_TOKEN_NAME, token.get());
                        authentication = new UsernamePasswordAuthenticationToken(user.get(), null, TokenUtil.getAuthorities(token.get()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (authentication == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "권한 없음");
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
    
    
    protected boolean checkPrivilege(String path, String[] privileges) {
        if (StringUtil.urlPatternCheck(path, privileges)) {
            return true;
        }
        return false;
        
    }
}
