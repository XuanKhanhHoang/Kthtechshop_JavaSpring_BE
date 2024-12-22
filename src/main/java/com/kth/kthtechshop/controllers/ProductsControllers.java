package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.product.GetProductsDTO;
import com.kth.kthtechshop.dto.product.ProductDTO;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsControllers {

    private final ProductService productService;

    @Autowired
    public ProductsControllers(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("get_products")
    public CompletableFuture<ListResponse<ProductDTO>> getAllProducts(@Valid @ModelAttribute GetProductsDTO query) {
        String nameSearchTerm = query.getKey_word() != null ? query.getKey_word() : "";
        Double rating = query.getRating() != null ? query.getRating() : 0.0;
        List<Long> categoryIds = query.getCategory_ids() != null ? query.getCategory_ids() : List.of();
        Integer brandId = query.getBrand_id() != null ? query.getBrand_id() : 0;
        Long minPrice = query.getMin_price() != null ? query.getMin_price() : 0L;
        Long maxPrice = query.getMax_price() != null ? query.getMax_price() : Long.MAX_VALUE;
        if (minPrice > maxPrice) throw new BadRequestException("min_price must be equal or smaller than max_price");
        String orderCol = query.getOrder_col() != null ? query.getOrder_col() : "id";
        String orderType = query.getOrder_type() != null ? query.getOrder_type().toUpperCase() : "ASC";
        int page = query.getPage() != null ? query.getPage() - 1 : 0;
        int limit = query.getLimit();
        return productService.findProducts(nameSearchTerm, rating, categoryIds, brandId, minPrice, maxPrice, orderCol, orderType, page, limit);
    }

    @GetMapping("get_product")
    public CompletableFuture<ProductDTO> getProduct(@RequestParam Long id) {
        if (id == null || id < 1) throw new BadRequestException();
        return productService.findProduct(id);
    }
}
