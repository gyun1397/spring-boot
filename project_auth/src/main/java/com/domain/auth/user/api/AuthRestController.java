package com.domain.auth.user.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.common.util.CookieUtil;
import com.common.util.ResponseEntityUtil;
import com.common.util.TokenUtil;
import com.common.vo.TokenVO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {
    
    private final UserService    userService;
    
    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = true) LoginVO loginVO ) throws Exception {
        userService.logout();
        TokenVO token = userService.login(loginVO.getUserId(), loginVO.getPassword());
        CookieUtil.addCookie(response, "accessToken", token.getAccessToken());
        CookieUtil.addCookie(response, "refreshToken", token.getRefreshToken());
        return ResponseEntityUtil.ok(token);
    }
    
    @GetMapping("/logout")
    public @ResponseBody ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        userService.logout();
        return ResponseEntityUtil.ok();
    }
    
    @PostMapping("/refresh")
    public @ResponseBody ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TokenVO token = userService.refresh(TokenUtil.getToken());
        CookieUtil.addCookie(response, "accessToken", token.getAccessToken());
        CookieUtil.addCookie(response, "refreshToken", token.getRefreshToken());
        return ResponseEntityUtil.ok(token);
    }

}

class LoginVO {
    private String userId;
    private String password;
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}

