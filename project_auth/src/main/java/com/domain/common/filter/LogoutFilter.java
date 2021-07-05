package com.domain.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class LogoutFilter  extends OncePerRequestFilter {
    private static Set<String> ALLOWED_PATHS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList("/auth/login", "/auth/logout")));
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO Auto-generated method stub
        
    }
    
}
