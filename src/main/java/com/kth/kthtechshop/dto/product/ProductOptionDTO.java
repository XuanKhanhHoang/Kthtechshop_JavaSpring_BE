package com.kth.kthtechshop.dto.product;


import com.kth.kthtechshop.models.ProductOption;
import lombok.*;

@Getter
@Setter
public class ProductOptionDTO {
    private Long id;
    private String name;
    private String image;
    private Long sellPrice;
    private Integer amount;
    private Integer discount;

    public ProductOptionDTO(ProductOption productOption) {
        this.amount = productOption.getAmount();
        this.name = productOption.getName();
        this.discount = productOption.getDiscount();
        this.image = productOption.getImage();
        this.sellPrice = productOption.getSellPrice();
        this.id = productOption.getId();
    }
}
