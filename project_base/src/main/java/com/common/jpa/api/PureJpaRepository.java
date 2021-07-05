package com.common.jpa.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PureJpaRepository<T, ID> extends JpaRepository<T, ID> {
    
    default T findOne(ID id) {
        return (T) findById(id).orElse(null);
    }

    
}