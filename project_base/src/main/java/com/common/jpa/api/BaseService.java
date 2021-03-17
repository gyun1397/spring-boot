package com.common.jpa.api;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.common.jpa.domain.BasicEntity;

public interface BaseService<T extends BasicEntity<ID>, ID> {
    public void deleteById(ID id) throws Exception;

    public void deleteAllById(List<ID> ids) throws Exception;

    public void deleteByEntity(T entity) throws Exception;

    public void deleteAllByEntity(List<T> entities) throws Exception;

    public void deleteAll(Map<String, Object> map) throws Exception;

    public T createRequest(T entity) throws Exception;

    public <D> D createDtoed(D dto) throws Exception;

    public List<T> createAllRequest(List<T> entities) throws Exception;

    public <D> List<D> createAllDtoed(List<D> dtos) throws Exception;

    public T updateRequest(T entity) throws Exception;

    public <D> D updateDtoed(D dto) throws Exception;

    public List<T> updateAllRequest(List<T> entities) throws Exception;

    public <D> List<D> updateAllDtoed(List<D> dtos) throws Exception;

    public T updateRequestById(ID id, Map<String, Object> map) throws Exception;

    public <D> D updateDtoedById(ID id, Map<String, Object> map, Class<D> dtoClass) throws Exception;

    public T updateRequestByEntity(T entity, Map<String, Object> map) throws Exception;

    public <D> D updateDtoedByEntity(D dto, Map<String, Object> map) throws Exception;

    public void duplicationCheck(T entity) throws Exception;

    public void beforeCreate(T entity) throws Exception;

    public void afterCreate(T entity) throws Exception;

    public void beforeUpdate(T entity) throws Exception;

    public void afterUpdate(T entity) throws Exception;

    public T findOne(ID id) throws Exception;

    public <D> D findOneDtoed(T entity, Class<D> dtoClass) throws Exception;

    public <D> D findOneDtoed(ID id, Class<D> dtoClass) throws Exception;

    public T findOne(Map<String, Object> map) throws Exception;

    public <D> D findOneDtoed(Map<String, Object> map, Class<D> dtoClass) throws Exception;

    public List<T> findAll(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> findAll(Map<String, Object> map, List<String> fields) throws Exception;

    public <D> List<D> findAllDtoed(Map<String, Object> map, Class<D> dtoClass) throws Exception;

    public Page<T> search(Map<String, Object> map, Pageable pageable) throws Exception;

    public Page<Map<String, Object>> search(Map<String, Object> map, List<String> fields, Pageable pageable) throws Exception;

    public <D> Page<D> searchDtoed(Map<String, Object> map, Pageable pageable, Class<D> dtoClass) throws Exception;

    public Class<?> getClassType() throws Exception;
    
    public void initId(Map<String, Object> map) throws Exception;
    
    public Sort getSort(Map<String, Object> map) throws Exception;
}
