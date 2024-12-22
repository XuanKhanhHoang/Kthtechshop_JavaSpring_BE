package com.kth.kthtechshop.repository;

import com.kth.kthtechshop.models.Category;
import com.kth.kthtechshop.models.Product;
import com.kth.kthtechshop.models.ProductOption;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecification {
    public static Specification<Product> hasNameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasRatingGreaterThanOrEqual(Double rating) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    public static Specification<Product> belongsToCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> categories = root.join("categories");
            return categories.get("id").in(categoryIds);
        };
    }

    public static Specification<Product> belongsToBrand(int brandId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("brand").get("id"), brandId);
    }

    private static Specification<Product> hasPriceBetween(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, ProductOption> productOptions = root.join("productOptions");
            if (minPrice != null && maxPrice != null)
                return criteriaBuilder.between(productOptions.get("sellPrice"), minPrice, maxPrice);
            if (minPrice != null)
                return criteriaBuilder.greaterThanOrEqualTo(productOptions.get("sellPrice"), minPrice);
            if (maxPrice != null)
                return criteriaBuilder.lessThanOrEqualTo(productOptions.get("sellPrice"), maxPrice);
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Product> getSpecifications(String nameSearchTerm, Double rating, List<Long> categoryIds, int brandId, Long minPrice, Long maxPrice) {
        Specification<Product> specification = Specification.where(null);
        if (nameSearchTerm != null && !nameSearchTerm.isEmpty()) {
            specification = specification.and(hasNameLike(nameSearchTerm));
        }
        if (rating != null) {
            specification = specification.and(hasRatingGreaterThanOrEqual(rating));
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            specification = specification.and(belongsToCategories(categoryIds));
        }
        if (brandId > 0) {
            specification = specification.and(belongsToBrand(brandId));
        }
        if (minPrice != null || maxPrice != null) {
            specification = specification.and(hasPriceBetween(minPrice, maxPrice));
        }
        return specification;
    }


}
