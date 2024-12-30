package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.CategoryDTO;
import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.product.CreateProductDTO;
import com.kth.kthtechshop.dto.product.ProductDTO;
import com.kth.kthtechshop.dto.product.UpdateProductDetailDTO;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.models.Category;
import com.kth.kthtechshop.models.Product;
import com.kth.kthtechshop.models.ProductOption;
import com.kth.kthtechshop.repository.BrandRepository;
import com.kth.kthtechshop.repository.CategoryRepository;
import com.kth.kthtechshop.repository.ProductRepository;
import com.kth.kthtechshop.repository.ProductSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, BrandRepository brandRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
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

    @Transactional
    public List<Long> createProduct(CreateProductDTO newProduct) {
        Product product = new Product();
        product.setName(newProduct.getName());
        product.setDescription(newProduct.getDescription());
        product.setInformation(newProduct.getInformation());
        product.setBrand(brandRepository.findById(newProduct.getBrandId()).orElse(null));
        List<Category> categories = categoryRepository.findAllById(Arrays.stream(newProduct.getCategory()).boxed().toList());
        product.setCategories(categories);
        List<ProductOption> productOptions = Arrays.stream(newProduct.getOptions()).map(optionDTO -> {
            ProductOption option = new ProductOption();
            option.setName(optionDTO.getName());
            option.setDiscount(optionDTO.getDiscount() != null ? optionDTO.getDiscount() : 0);
            option.setAmount(optionDTO.getAmount());
            option.setSellPrice(optionDTO.getSellPrice());
            return option;
        }).toList();
        product.setProductOptions(productOptions);
        var res = productRepository.save(product);
        List<Long> ids = new ArrayList<>();
        ids.add(res.getId());
        res.getProductOptions().forEach(productOption -> ids.add(productOption.getId()));
        return ids;
    }

    public void updateProductImage(long productId, String id) {
        var prod = productRepository.findById(productId);
        if (prod.isEmpty()) throw new NotFoundException();
        Product product = prod.get();
        product.setImage(id);
        productRepository.save(product);
    }

    @Transactional
    public void updateProductDetail(@Valid UpdateProductDetailDTO product) {
        var prod = productRepository.findById(product.getId());
        if (prod.isEmpty()) throw new NotFoundException();
        Product saveProd = prod.get();
        saveProd.setName(product.getName());
        saveProd.setDescription(product.getDescription());
        saveProd.setInformation(product.getInformation());
        saveProd.setBrand(brandRepository.findById(product.getBrandId()).orElse(null));
        List<Category> categories = categoryRepository.findAllById(Arrays.stream(product.getCategory()).boxed().toList());
        saveProd.setCategories(categories);
    }

    @Transactional
    public void updateProductOptionImage(long prodId, List<String> imgIds) {
        var prod = productRepository.findById(prodId);
        if (prod.isEmpty()) throw new NotFoundException();
        var prd = prod.get();
        List<ProductOption> productOptions = prd.getProductOptions();
        for (int i = 0; i < imgIds.size(); i++) {
            productOptions.get(i).setImage(imgIds.get(i));
        }
        productRepository.save(prd);
    }
}