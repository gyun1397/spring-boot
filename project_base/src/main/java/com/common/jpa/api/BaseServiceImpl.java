package com.common.jpa.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.common.jpa.domain.BasicEntity;
import com.common.util.DataUtil;
import com.common.util.ObjectUtil;
import com.common.util.StringUtil;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;

public abstract class BaseServiceImpl<T extends BasicEntity<ID>, ID> extends QuerydslRepositorySupport implements BaseService<T, ID> {
    
    @Autowired
    private BaseJpaRepository<T, ID> baseRepository;
    
    private Class<T> classType;

    public BaseServiceImpl(Class<T> domainClass) {
        super(domainClass);
        this.classType = domainClass;
    }
    
    public static <T> PageImpl<T> getPageImpl(Pageable pageable, JPQLQuery<T> query) {
        Long total = query.fetchCount();
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        List<T> content = total > pageable.getOffset() ? query.fetch() : Collections.<T> emptyList();
        return new PageImpl<T>(content, pageable, total);
    }


    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void deleteById(ID id) throws Exception {
        T entity = baseRepository.findOne(id);
        deleteByEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void deleteAllById(List<ID> ids) throws Exception {
        List<T> entities = baseRepository.findAllById(ids);
        for (T entity : entities) {
            deleteByEntity(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void deleteByEntity(T entity) throws Exception {
        baseRepository.delete(entity);
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void deleteAllByEntity(List<T> entities) throws Exception {
        for (T entity : entities) {
            deleteByEntity(entity);
        }
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void deleteAll(Map<String, Object> map) throws Exception {
        List<T> entities = findAll(map);
        for (T entity : entities) {
            deleteByEntity(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public T createRequest(T entity) throws Exception {
        entity.setId(null);
        duplicationCheck(entity);
        beforeCreate(entity);
        entity = baseRepository.saveEntity(entity);
        afterCreate(entity);
        return entity;
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public <D> D createDtoed(D dto) throws Exception {
        T entity = ObjectUtil.convertBean(dto, classType);
        entity = createRequest(entity);
        return (D) ObjectUtil.convertObject(entity, dto.getClass());
    }
    
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public List<T> createAllRequest(List<T> entities) throws Exception {
        List<T> result = new ArrayList<>();
        for (T entity : entities) {
            entity = createRequest(entity);
            result.add(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public <D> List<D> createAllDtoed(List<D> dtos) throws Exception {
        List<D> result = new ArrayList<>();
        for (D dto : dtos) {
            dto = createDtoed(dto);
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public T updateRequest(T entity) throws Exception {
        duplicationCheck(entity);
        beforeUpdate(entity);
        entity = baseRepository.saveEntity(entity);
        afterUpdate(entity);
        return entity;
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public <D> D updateDtoed(D dto) throws Exception {
        T entity = ObjectUtil.convertBean(dto, classType);
        entity = updateRequest(entity);
        return (D) ObjectUtil.convertObject(entity, dto.getClass());
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public List<T> updateAllRequest(List<T> entities) throws Exception {
        List<T> result = new ArrayList<>();
        for (T entity : entities) {
            entity = updateRequest(entity);
            result.add(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public <D> List<D> updateAllDtoed(List<D> dtos) throws Exception {
        List<D> result = new ArrayList<>();
        for (D dto : dtos) {
            dto = updateDtoed(dto);
            result.add(dto);
        }
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public T updateRequestById(ID id, Map<String, Object> map) throws Exception {
        T entity = baseRepository.findOne(id);
        return updateRequestByEntity(entity, map);
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public <D> D updateDtoedById(ID id, Map<String, Object> map, Class<D> dtoClass) throws Exception {
        T entity = updateRequestById(id, map);
        return ObjectUtil.convertObject(entity, dtoClass);
    }
    

    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public T updateRequestByEntity(T entity, Map<String, Object> map) throws Exception {
        initId(map);
        ObjectUtil.populate(entity, map);
        duplicationCheck(entity);
        beforeUpdate(entity);
        entity = baseRepository.saveEntity(entity);
        afterUpdate(entity);
        return entity;
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public <D> D updateDtoedByEntity(D dto, Map<String, Object> map) throws Exception {
        T entity = ObjectUtil.convertBean(dto, classType);
        entity = updateRequestByEntity(entity, map);
        return (D) ObjectUtil.convertObject(entity, dto.getClass());
    }

    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void duplicationCheck(T entity) throws Exception {
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void beforeCreate(T entity) throws Exception {
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void afterCreate(T entity) throws Exception {
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void beforeUpdate(T entity) throws Exception {
    }
    
    @Override
    @Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
    public void afterUpdate(T entity) throws Exception {
    }

    @Override
    public T findOne(Map<String, Object> map) throws Exception {
        Predicate predicate = baseRepository.makeWhereCondition(map);
        Iterable<T> entities = baseRepository.findAll(predicate, getSort(map));
        if (entities.iterator().hasNext()) {
            return entities.iterator().next();
        }
        return null;
    }
    
    @Override
    public <D> D findOneDtoed(Map<String, Object> map, Class<D> dtoClass) throws Exception {
        return ObjectUtil.convertObject(findOne(map), dtoClass);
    }
    
    
    @Override
    public <D> D findOneDtoed(T entity, Class<D> dtoClass) throws Exception {
        return ObjectUtil.convertObject(entity, dtoClass);
    }

    @Override
    public T findOne(ID id) throws Exception {
        return baseRepository.findOne(id);
    }
    
    @Override
    public <D> D findOneDtoed(ID id, Class<D> dtoClass) throws Exception {
        return ObjectUtil.convertObject(findOne(id), dtoClass);
    }

    @Override
    public List<T> findAll(Map<String, Object> map) throws Exception {
        Predicate predicate = baseRepository.makeWhereCondition(map);
        Iterable<T> entities = baseRepository.findAll(predicate, getSort(map));
        return ObjectUtil.convertIterableToList(entities);
    }
    
    @Override
    public List<Map<String, Object>> findAll(Map<String, Object> map, List<String> fields) throws Exception {
        Predicate predicate = baseRepository.makeWhereCondition(map);
        List<Map<String, Object>> entities = baseRepository.findAll(fields, predicate, getSort(map));
        return entities;
    }

    @Override
    public <D> List<D> findAllDtoed(Map<String, Object> map, Class<D> dtoClass) throws Exception {
        return ObjectUtil.convertCollection(findAll(map), dtoClass);
    }

    @Override
    public Page<T> search(Map<String, Object> map, Pageable pageable) throws Exception {
        Predicate predicate = baseRepository.makeWhereCondition(map);
        Page<T> entity = baseRepository.findAll(predicate, pageable);
        return entity;
    }

    @Override
    public Page<Map<String, Object>> search(Map<String, Object> map, List<String> fields, Pageable pageable) throws Exception {
        Predicate predicate = baseRepository.makeWhereCondition(map);
        Page<Map<String, Object>> entity = baseRepository.findAll(fields, predicate, pageable);
        return entity;
    }

    @Override
    public <D> Page<D> searchDtoed(Map<String, Object> map, Pageable pageable, Class<D> dtoClass) throws Exception {
        return ObjectUtil.convertPage(search(map, pageable), dtoClass);
    }
    
    @Override
    public Class<?> getClassType() throws Exception {
        return this.classType;
    }
    
    @Override
    public void initId(Map<String, Object> map) throws Exception  {
        map.remove("id");
        map.remove("updateId");
        map.remove("createId");
    }
    
    @Override
    public Sort getSort(Map<String, Object> map) throws Exception  {
        String sort = DataUtil.stringConvert(map.get("sort"));
        Direction direction = Direction.DESC;
        if (sort == null) {
            return Sort.by(direction, "id");
        }
        String[] orderArray = StringUtil.split(sort, ",");
        List<String> orderList = new ArrayList<>(Arrays.asList(orderArray));
        String lastValue = orderList.get(orderList.size()-1);
        if (StringUtil.in(lastValue.toUpperCase(), "ASC", "DESC")) {
            direction = Direction.valueOf(lastValue.toUpperCase());
            orderList.remove(lastValue);
        } else {
            direction = Direction.ASC;
        }
        return Sort.by(direction, orderList.toArray(new String[orderList.size()]));
    }
}
