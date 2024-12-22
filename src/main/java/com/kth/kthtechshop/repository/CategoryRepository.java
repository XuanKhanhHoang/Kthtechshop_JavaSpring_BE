package com.kth.kthtechshop.repository;

import com.kth.kthtechshop.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
