package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.BrandDTO;
import com.kth.kthtechshop.dto.CategoryDTO;
import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.models.Category;
import com.kth.kthtechshop.repository.BrandRepository;
import com.kth.kthtechshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CommonService {
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Autowired
    public CommonService(CategoryRepository categoryRepository, BrandRepository brandRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    @Async
    public CompletableFuture<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = this.categoryRepository.findAll().stream().map(category -> new CategoryDTO(category.getId(), category.getName(), category.getIcon())).toList();
        return CompletableFuture.completedFuture(categories);
    }

    @Async
    public CompletableFuture<List<BrandDTO>> getBrands() {
        List<BrandDTO> brands = this.brandRepository.findAll().stream().map(brand -> new BrandDTO(brand.getId(), brand.getName(), brand.getIcon())).toList();
        return CompletableFuture.completedFuture(brands);
    }
}
