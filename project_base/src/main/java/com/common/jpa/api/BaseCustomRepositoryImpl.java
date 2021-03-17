package com.common.jpa.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.querydsl.QSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;

//@RequiredArgsConstructor
public abstract class BaseCustomRepositoryImpl<T, Q extends EntityPathBase<T>> implements BaseCustomRepository {
    
    @Autowired
    private JPQLQueryFactory jpqlFactory;
    
    public Q qEntity;
    
    public BaseCustomRepositoryImpl(Q qEntity) {
        this.qEntity = qEntity;
    }
    
    @Override
    public List<Map<String, Object>> findAll(List<String> fields, Predicate predicate) {
        JPQLQuery<Tuple> jPQLQuery = selectQuery(fields, predicate);
        List<Tuple> results = jPQLQuery.fetch();
        return getResult(fields, results);
    }
    
    @Override
    public List<Map<String, Object>> findAll(List<String> fields, Predicate predicate, Sort sort) {
        JPQLQuery<Tuple> jPQLQuery = applySorting(selectQuery(fields, predicate), sort);
        List<Tuple> results = jPQLQuery.fetch();
        List<Map<String, Object>> result = getResult(fields, results);
        return result;
    }
    
    @Override
    public List<Map<String, Object>> findAll(List<String> fields, Predicate predicate, Order... orders) {
        JPQLQuery<Tuple> jPQLQuery  = applySorting(selectQuery(fields, predicate));
        List<Tuple> results = jPQLQuery.fetch();
        List<Map<String, Object>> result = getResult(fields, results);
        return result;
    }
    
    @Override
    public Page<Map<String, Object>> findAll(List<String> fields, Predicate predicate, Pageable pageable) {
        JPQLQuery<Tuple> jPQLQuery  = applyPagination(selectQuery(fields, predicate), pageable);
        List<Tuple> results = jPQLQuery.fetch();
        List<Map<String, Object>> result = getResult(fields, results);
        return new PageImpl<Map<String, Object>>(result, pageable, 0);
    }
   
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OrderSpecifier<?> toOrderSpecifier(Order order) {
        return new OrderSpecifier(com.querydsl.core.types.Order.valueOf(order.getDirection().name()), getExpression(order.getProperty()));
    }
    
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OrderSpecifier<?> toOrderSpecifier(String direction, String field) {
        return new OrderSpecifier(com.querydsl.core.types.Order.valueOf(direction.toUpperCase()), getExpression(field));
    }

    @Override
    public JPQLQuery<Tuple> applyPagination(JPQLQuery<Tuple> jPQLQuery, Pageable pageable) {
        if (pageable.isUnpaged()) {
            return jPQLQuery;
        }
        jPQLQuery.offset(pageable.getOffset());
        jPQLQuery.limit(pageable.getPageSize());
        
        return applySorting(jPQLQuery, pageable.getSort());
    }
    
    @Override
    public JPQLQuery<Tuple> applySorting(JPQLQuery<Tuple> jPQLQuery, Sort sort) {
        if (sort.isUnsorted()) {
            return jPQLQuery;
        }
        if (sort instanceof QSort) {
            return addOrderByFrom((QSort) sort, jPQLQuery);
        }
        return addOrderByFrom(sort, jPQLQuery);
    }
    
    @Override
    public JPQLQuery<Tuple> applySorting(JPQLQuery<Tuple> jPQLQuery, Order... orders) {
        for (Order order : orders) {
            jPQLQuery.orderBy(toOrderSpecifier(order));
        }
        return jPQLQuery;
    }
    
    @Override
    public JPQLQuery<Tuple> addOrderByFrom(Sort sort, JPQLQuery<Tuple> jPQLQuery) {
        for (Order order : sort) {
            jPQLQuery.orderBy(toOrderSpecifier(order));
        }
        return jPQLQuery;
    }
    
    @Override
    public JPQLQuery<Tuple> addOrderByFrom(QSort qsort, JPQLQuery<Tuple> query) {
        List<OrderSpecifier<?>> orderSpecifiers = qsort.getOrderSpecifiers();
        return query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
    }
    
    @Override
    @SuppressWarnings("serial")
    public List<Map<String, Object>> getResult(List<String> fields, List<Tuple> results) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tuple row : results) {
            result.add(new HashMap<String, Object>(){{
                for (String field : fields) {
                    put(field, row.get(getExpression(field)));
                }
            }});
        }
        return result;
    }
    
    @Override
    public Expression<?>[] getSelectCondition(List<String> fields) {
        List<Expression<?>> expressions = new ArrayList<>();
        for (String field : fields) {
            expressions.add(getExpression(field));
        }
        return expressions.toArray(new Expression<?>[expressions.size()]);
    }
    
    @Override
    public JPQLQuery<Tuple> selectQuery(List<String> fields, Predicate predicate) {
        return jpqlFactory.select(getSelectCondition(fields)).from(qEntity).where(predicate);
    }
    
    @Override
    public abstract Expression<?> getExpression(String field);

    @Override
    public abstract BooleanBuilder makeWhereCondition(Map<String, Object> map);
}