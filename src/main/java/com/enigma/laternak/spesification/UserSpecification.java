package com.enigma.laternak.spesification;

import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> getSpecification(PaginationUserRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getCustomerName() != null) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("customerName")),
                                "%" + request.getCustomerName().toLowerCase() + "%"
                        )
                );
            }

            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
