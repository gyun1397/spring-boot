package com.common.jpa.api;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.querydsl.QSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;

public interface BaseCustomRepository {
    
    public List<Map<String, Object>> findAll(List<String> fields, Predicate predicate);

    public List<Map<String, Object>> findAll(List<String> fields, Predicate predicate, Sort sort);

    public List<Map<String, Object>> findAll(List<String> fields, Predicate predicate, Order... orders);
    
    public Page<Map<String, Object>> findAll(List<String> fields, Predicate predicate, Pageable pageable);
    
    public OrderSpecifier<?> toOrderSpecifier(Order order);
    
    public OrderSpecifier<?> toOrderSpecifier(String direction, String field);
    
    public JPQLQuery<Tuple> applyPagination(JPQLQuery<Tuple> jPQLQuery, Pageable pageable);
    
    public JPQLQuery<Tuple> applySorting(JPQLQuery<Tuple> jPQLQuery, Sort sort);
    
    public JPQLQuery<Tuple> applySorting(JPQLQuery<Tuple> jPQLQuery, Order... orders);
    
    public JPQLQuery<Tuple> addOrderByFrom(Sort sort, JPQLQuery<Tuple> jPQLQuery);
    
    public JPQLQuery<Tuple> addOrderByFrom(QSort qsort, JPQLQuery<Tuple> query);
    
    public List<Map<String, Object>> getResult(List<String> fields, List<Tuple> results);
    
    public Expression<?>[] getSelectCondition(List<String> fields);
    
    public JPQLQuery<Tuple> selectQuery(List<String> fields, Predicate predicate);
    
    public Expression<?> getExpression(String field);

    public BooleanBuilder makeWhereCondition(Map<String, Object> map);
    
//    default List<Map<String, Object>> selectAll(List<String> fields, Predicate predicate) {
//        JPQLQuery<Tuple> jPQLQuery = selectQuery(fields, predicate);
//        List<Tuple> results = jPQLQuery.fetch();
//        return getResult(fields, results);
//    }
//
//    default List<Map<String, Object>> selectAll(List<String> fields, Predicate predicate, Sort sort) {
//        JPQLQuery<Tuple> jPQLQuery = applySorting(selectQuery(fields, predicate), sort);
//        List<Tuple> results = jPQLQuery.fetch();
//        List<Map<String, Object>> result = getResult(fields, results);
//        return result;
//    }
//
//    default List<Map<String, Object>> selectAll(List<String> fields, Predicate predicate, Order... orders) {
//        JPQLQuery<Tuple> jPQLQuery  = applySorting(selectQuery(fields, predicate));
//        List<Tuple> results = jPQLQuery.fetch();
//        List<Map<String, Object>> result = getResult(fields, results);
//        return result;
//    }
//
//    default Page<Map<String, Object>> selectAll(List<String> fields, Predicate predicate, Pageable pageable) {
//        JPQLQuery<Tuple> jPQLQuery  = applyPagination(selectQuery(fields, predicate), pageable);
//        List<Tuple> results = jPQLQuery.fetch();
//        List<Map<String, Object>> result = getResult(fields, results);
//        return new PageImpl<Map<String, Object>>(result, pageable, 0);
//    }
//    
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    default OrderSpecifier<?> toOrderSpecifier(Order order) {
//        return new OrderSpecifier(com.querydsl.core.types.Order.valueOf(order.getDirection().name()), getExpression(order.getProperty()));
//    }
//    
//    default JPQLQuery<Tuple> applyPagination(JPQLQuery<Tuple> jPQLQuery, Pageable pageable) {
//        if (pageable.isUnpaged()) {
//            return jPQLQuery;
//        }
//        jPQLQuery.offset(pageable.getOffset());
//        jPQLQuery.limit(pageable.getPageSize());
//        
//        return applySorting(jPQLQuery, pageable.getSort());
//    }
//    
//    default JPQLQuery<Tuple> applySorting(JPQLQuery<Tuple> jPQLQuery, Sort sort) {
//        if (sort.isUnsorted()) {
//            return jPQLQuery;
//        }
//        if (sort instanceof QSort) {
//            return addOrderByFrom((QSort) sort, jPQLQuery);
//        }
//        return addOrderByFrom(sort, jPQLQuery);
//    }
//    
//    default JPQLQuery<Tuple> applySorting(JPQLQuery<Tuple> jPQLQuery, Order... orders) {
//        for (Order order : orders) {
//            jPQLQuery.orderBy(toOrderSpecifier(order));
//        }
//        return jPQLQuery;
//    }
//    
//    default JPQLQuery<Tuple> addOrderByFrom(Sort sort, JPQLQuery<Tuple> jPQLQuery) {
//        for (Order order : sort) {
//            jPQLQuery.orderBy(toOrderSpecifier(order));
//        }
//        return jPQLQuery;
//    }
//    
//    default JPQLQuery<Tuple> addOrderByFrom(QSort qsort, JPQLQuery<Tuple> query) {
//        List<OrderSpecifier<?>> orderSpecifiers = qsort.getOrderSpecifiers();
//        return query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
//    }
//    
//    default List<Map<String, Object>> getResult(List<String> fields, List<Tuple> results) {
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (Tuple row : results) {
//            result.add(new HashMap<String, Object>(){{
//                for (String field : fields) {
//                    put(field, row.get(getExpression(field)));
//                }
//            }});
//        }
//        return result;
//    }
//    
//    default Expression<?>[] getSelectCondition(List<String> fields) {
//        List<Expression<?>> expressions = new ArrayList<>();
//        for (String field : fields) {
//            expressions.add(getExpression(field));
//        }
//        return expressions.toArray(new Expression<?>[expressions.size()]);
//    }
//    
//    JPQLQuery<Tuple> selectQuery(List<String> fields, Predicate predicate);
//    
//    Expression<?> getExpression(String field);
}