package com.kth.kthtechshop.repository;

import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.models.Category;
import com.kth.kthtechshop.models.Order;
import com.kth.kthtechshop.models.Product;
import com.kth.kthtechshop.models.ProductOption;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class OrderSpecification {
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) ->
        {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> belongsToUser(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }


    public static Specification<Order> getSpecifications(Long userId, OrderStatus orderStatus) {
        Specification<Order> specification = Specification.where(null);
        if (userId != null) {
            specification = specification.and(belongsToUser(userId));
        }
        if (orderStatus != null) {
            specification = specification.and(hasStatus(orderStatus));
        }

        return specification;
    }


}
