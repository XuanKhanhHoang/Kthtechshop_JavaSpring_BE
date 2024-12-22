package com.kth.kthtechshop.dto.product;

import com.kth.kthtechshop.dto.CategoryDTO;
import com.kth.kthtechshop.models.Product;
import com.kth.kthtechshop.models.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String image;
    private String description;
    private Date createAt;
    private Double rating;
    private String information;
    private List<CategoryDTO> categories;
    private List<ProductOptionDTO> productOptions;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.image = product.getImage();
        this.name = product.getName();
        this.description = product.getDescription();
        this.createAt = product.getCreateAt();
        this.rating = product.getRating();
        this.information = product.getInformation();

        this.categories = product.getCategories().stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getIcon())).toList();
        this.productOptions =
                product.getProductOptions().stream().map(ProductOptionDTO::new).toList();
    }
}

