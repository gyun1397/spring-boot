package com.common.jpa.api;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import com.common.jpa.domain.BasicEntity;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BasicEntity<ID>, ID> extends PureJpaRepository<T, ID>, BaseCustomRepository {
    
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