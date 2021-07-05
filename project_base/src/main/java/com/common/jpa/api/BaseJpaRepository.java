package com.common.jpa.api;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.common.jpa.domain.BasicEntity;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BasicEntity<ID>, ID> extends PureJpaRepository<T, ID>, QuerydslPredicateExecutor<T>, BaseCustomRepository {

    default T findOneByPredicate(Predicate predicate) {
        return (T) findOne(predicate).orElse(null);
    }
    
    List<T> findAll(Predicate predicate);
    
    List<T> findAll(Predicate predicate, Sort sort);
    
    default <S extends T> S saveEntity(S entity) {
        entity.init();
        return save(entity);
    }

    default <S extends T> List<S> saveAllEntity(Iterable<S> entities) {
        for (S entity : entities) {
            entity.init();
        }
        return saveAll(entities);
    }

    default <S extends T> S saveEntityAndFlush(S entity) {
        entity.init();
        return saveAndFlush(entity);
    }
    
    
    
    /*
     * <D> D findDtoedById(Integer id, Class<D> dto);
     * <D> List<D> findDtos(Class<D> dto);
     * <D> Page<D> findDtosSearch(Class<D> dto, Pageable pageable);
     * <D> Page<D> findDtosSearch(Class<D> dto, Pageable pageable, Predicate predicate);
     */
}