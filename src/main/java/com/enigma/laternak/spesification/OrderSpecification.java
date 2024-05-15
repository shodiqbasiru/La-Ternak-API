package com.enigma.laternak.spesification;

import com.enigma.laternak.dto.request.OrderSpecificationRequest;
import com.enigma.laternak.dto.request.PaginationOrderRequest;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.util.DateUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> getSpecification(PaginationOrderRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStartDate() != null && request.getEndDate() != null) {
                Date startDate = DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd");
                Date endDate = DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd");
                Predicate period = criteriaBuilder.between(root.get("orderDate"), startDate, new Timestamp(endDate.getTime() + 86400000));
                predicates.add(period);
            } else if (request.getStartDate() != null) {
                Date startDate = DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd");
                Predicate periodGreater = criteriaBuilder.greaterThan(root.get("orderDate"), startDate);
                predicates.add(periodGreater);
            } else if (request.getEndDate() != null) {
                Date endDate = DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd");
                Predicate periodLess = criteriaBuilder.lessThan(root.get("orderDate"), endDate);
                predicates.add(periodLess);
            } else if (request.getOrderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderStatus"), request.getOrderStatus()));
            } else if (request.getStoreId() != null) {
                Join<Order, OrderDetail> orderDetailJoin = root.join("orderDetails");
                Join<OrderDetail, Product> productJoin = orderDetailJoin.join("product");
                Join<Product, Store> storeJoin = productJoin.join("store");
                predicates.add(criteriaBuilder.equal(storeJoin.get("id"), request.getStoreId()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };


    }

    public static Specification<Order> getSpecification(OrderSpecificationRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getOrderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderStatus"), request.getOrderStatus()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
