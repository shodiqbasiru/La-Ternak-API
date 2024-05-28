package com.enigma.laternak.spesification;

import com.enigma.laternak.dto.request.ProductSpecificationRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> getSpecification(SearchProductRequest request){
        return (root, query, cb) -> {
            List<Predicate> predicates=new ArrayList<>();
            if (request.getProductName()!=null){
                Predicate predicate=cb.like(cb.lower(root.get("productName")), "%"+request.getProductName().toLowerCase()+"%");
                predicates.add(predicate);
            }
            if (request.getPrice() != null){
                Predicate predicate=cb.equal(root.get("price"), request.getPrice());
                predicates.add(predicate);
            }
            if (request.getDescription() != null){
                Predicate predicate=cb.equal(root.get("description"), request.getDescription());
                predicates.add(predicate);
            }
            if (request.getStock() != null){
                Predicate predicate=cb.equal(root.get("stock"), request.getStock());
                predicates.add(predicate);
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }

    public static Specification<Product> getSpecification(ProductSpecificationRequest request){
        return (root, query, cb) -> {
            List<Predicate> predicates=new ArrayList<>();
            if (request.getProductName()!=null){
                Predicate predicate=cb.like(cb.lower(root.get("productName")), "%"+request.getProductName().toLowerCase()+"%");
                predicates.add(predicate);
            }
            if(request.getMinPrice() != null){
                Predicate predicate=cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice());
                predicates.add(predicate);
            }
            if(request.getMaxPrice() != null){
                Predicate predicate=cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice());
                predicates.add(predicate);
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
