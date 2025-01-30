package com.findmymeme.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.List;

public class QuerydslSortUtil {

    public static  OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, PathBuilder<?> pathBuilder) {
        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    return new OrderSpecifier(direction, pathBuilder.get(order.getProperty()));
                })
                .toArray(OrderSpecifier[]::new);
    }
}