package com.kth.kthtechshop.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class CreateProductOptionDTO {
    @NotBlank
    private String name;
    @Min(0)
    private Long sellPrice;
    @Min(0)
    private Integer amount;
    private Integer discount;
}
