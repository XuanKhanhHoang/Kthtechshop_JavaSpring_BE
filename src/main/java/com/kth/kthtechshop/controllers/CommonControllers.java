package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.BrandDTO;
import com.kth.kthtechshop.dto.CategoryDTO;
import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/common")
public class CommonControllers {
    private final CommonService commonService;

    @Autowired
    CommonControllers(CommonService commonService) {
        this.commonService = commonService;
    }

    @GetMapping("/get_categories")
    public CompletableFuture<List<CategoryDTO>> getCategories() {
        return commonService.getCategories();
    }

    @GetMapping("/get_brands")
    public CompletableFuture<List<BrandDTO>> getBrands() {
        return commonService.getBrands();
    }
}
