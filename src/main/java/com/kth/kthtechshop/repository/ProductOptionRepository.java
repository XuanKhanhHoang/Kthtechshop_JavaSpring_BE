package com.kth.kthtechshop.repository;

import com.kth.kthtechshop.models.Product;
import com.kth.kthtechshop.models.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}

