package com.domain.auth.user.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.common.exception.BadRequestException;
import com.common.exception.UnauthorizedException;
import com.common.util.CookieUtil;
import com.common.util.RedisUtil;
import com.common.util.SessionUtil;
import com.common.util.StringUtil;
import com.common.util.TokenUtil;
import com.common.vo.TokenVO;
import com.domain.auth.user.User;
import com.domain.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public TokenVO login(String userId, String password) throws Exception {
        User user = userRepository.findOneByUserId(userId);
        if (user == null) {
            throw new BadRequestException("해당 ID를 찾을 수 없습니다.");
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("패스워드가 틀렸습니다.");
        }
        Map<String, Object> claims = getClaims(user);
        String accessToken = TokenUtil.makeAccessToken(claims);
        String refreshToken = TokenUtil.makeRefreshToken(claims);
        TokenVO tokenVO = new TokenVO(accessToken, refreshToken);
        RedisUtil.setDataExpire(SessionUtil.getSessionId(), refreshToken, TokenUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        return tokenVO;
    }

    private Map<String, Object> getClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenUtil.JWT_PAYLOAD_USERNAME, user.getUserId());
        return claims;
    }
    
    @Override
    public void logout() throws Exception {
        String sessionId = SessionUtil.getSessionId();
        if (StringUtil.isNotEmpty(sessionId) && RedisUtil.hasKey(sessionId)) {
            RedisUtil.deleteData(sessionId);
        }
    }

public static void main(String[] args) {
    System.out.println(new Date(1625210340000L));
}
    @Override
    public TokenVO refresh(String refreshToken) throws Exception {
        Optional<String> user = TokenUtil.verifyJWT(refreshToken);
        if (user.isPresent() && isValidToken(refreshToken)) {
            Map<String, Object> claims = TokenUtil.getPayload(refreshToken);
            String newAccessToken = TokenUtil.makeAccessToken(claims);
            TokenVO tokenVO = new TokenVO(newAccessToken, refreshToken);
            CookieUtil.addCookie("accessToken", newAccessToken);
            CookieUtil.addCookie("refreshToken", refreshToken);
            RedisUtil.setDataExpire(SessionUtil.getSessionId(), refreshToken, TokenUtil.getVaildAt(refreshToken));
            return tokenVO;
        }
        throw new UnauthorizedException();
    }
    
    private boolean isValidToken(String token) {
        if (StringUtil.isNotEmpty(token) && RedisUtil.hasKey(token)) {
            return false;
        }
        return true;
    }
  
}