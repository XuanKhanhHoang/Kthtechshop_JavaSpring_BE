package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.product.*;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.services.GoogleDriveService;
import com.kth.kthtechshop.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsControllers {

    private final ProductService productService;
    private final GoogleDriveService googleDriveService;

    @Autowired
    public ProductsControllers(ProductService productService, GoogleDriveService googleDriveService) {
        this.productService = productService;
        this.googleDriveService = googleDriveService;
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

    @PostMapping("/private/create_product")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public ResponseEntity<String> createProduct(@RequestParam("image") MultipartFile avatar, @RequestParam("images") List<MultipartFile> optionImages, @RequestBody @Valid CreateProductDTO newProduct) {
        var ids = productService.createProduct(newProduct);
        if (!avatar.isEmpty()) {
            try {
                String id = googleDriveService.uploadFile(avatar);
                productService.updateProductImage(ids.get(0), id);
            } catch (GeneralSecurityException | IOException e) {
                return ResponseEntity.status(206).body("upload image error");
            }
        }
        if (!optionImages.isEmpty()) {
            try {
                List<String> imgIds = new ArrayList<>();
                for (int i = 1; i <= optionImages.size(); i++) {
                    if (i >= ids.size()) break;
                    String id = googleDriveService.uploadFile(optionImages.get(i - 1));
                    imgIds.add(id);
                }
                productService.updateProductOptionImage(ids.get(0), imgIds);
            } catch (GeneralSecurityException | IOException e) {
                return ResponseEntity.status(206).body("upload image error");
            }
        }
        return ResponseEntity.status(201).body(String.format("product id %d created;", ids.get(0)));
    }

    @PostMapping("/private/update_product")
    @PreAuthorize("hasRole('ROLE_Admin')")
    @Async
    public ResponseEntity<?> updateProduct(@RequestParam("image") MultipartFile avatar, @RequestBody @Valid UpdateProductDetailDTO product) {
        this.productService.updateProductDetail(product);
        if (!avatar.isEmpty()) {
            try {
                String id = googleDriveService.uploadFile(avatar);
                productService.updateProductImage(product.getId(), id);
            } catch (GeneralSecurityException | IOException e) {
                return ResponseEntity.status(206).body("upload image error");
            }
        }
        return ResponseEntity.status(200).body("prod update successfully");
    }
}
