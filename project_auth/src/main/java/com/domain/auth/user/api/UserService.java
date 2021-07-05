package com.domain.auth.user.api;

import com.common.vo.TokenVO;

public interface UserService {

    public TokenVO login(String userId, String password) throws Exception;
    
    public void logout() throws Exception;
    
    public TokenVO refresh(String refreshToken) throws Exception;
    
    
}