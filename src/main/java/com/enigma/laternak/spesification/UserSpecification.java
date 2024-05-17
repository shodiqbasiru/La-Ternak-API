package com.enigma.laternak.spesification;

import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.Role;
import com.enigma.laternak.entity.User;
import jakarta.persistence.criteria.Join;
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

            if (request.getRole() != null) {
                Join<User, Account> accountJoin = root.join("account");
                Join<Account, Role> roleJoin = accountJoin.join("roles");
                predicates.add(
                        cb.equal(roleJoin.get("role"), request.getRole())
                );

            }


            return cq.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
