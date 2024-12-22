package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.CategoryDTO;
import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.product.ProductDTO;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.models.Product;
import com.kth.kthtechshop.repository.ProductRepository;
import com.kth.kthtechshop.repository.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Async
    @Transactional
    public CompletableFuture<ListResponse<ProductDTO>> findProducts(String nameSearchTerm, Double rating, List<Long> categoryIds, Integer brandId, Long minPrice, Long maxPrice, String orderCol, String orderType, int page, int perPage) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderType), orderCol);
        Pageable pageable = PageRequest.of(page, perPage, sort);
        Specification<Product> specification = ProductSpecification.getSpecifications(nameSearchTerm, rating, categoryIds, brandId, minPrice, maxPrice);
        Page<Product> productsPage = productRepository.findAll(specification, pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream()
                .map(ProductDTO::new
                )
                .toList();
        return CompletableFuture.completedFuture(new ListResponse<ProductDTO>(productDTOs, productsPage.getTotalPages()));
    }

    @Async
    @Transactional
    public CompletableFuture<ProductDTO> findProduct(Long id) {
        Optional<Product> productContainer = productRepository.findById(id);
        if (productContainer.isEmpty()) throw new NotFoundException();
        return CompletableFuture.completedFuture(new ProductDTO(productContainer.get()));
    }
}