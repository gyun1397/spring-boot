package com.domain.auth.user;

import com.common.jpa.api.PureJpaRepository;

public interface UserRepository extends PureJpaRepository<User, Long> {
    
    public User findOneByUserId(String userId);
}
