package com.common.jpa.api;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface PureJpaRepository<T, ID> extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {
// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    
    default T findOne(ID id) {
        return (T) findById(id).orElse(null);
    }

    default T findOneByPredicate(Predicate predicate) {
        return (T) findOne(predicate).orElse(null);
    }
    
    List<T> findAll(Predicate predicate);
    
    List<T> findAll(Predicate predicate, Sort sort);
    
}