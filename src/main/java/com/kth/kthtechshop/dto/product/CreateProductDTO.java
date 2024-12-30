package com.kth.kthtechshop.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductDTO extends MainProductDetailDTO {

    @Size(min = 1)
    private CreateProductOptionDTO[] options;

    public CreateProductDTO(String name, String description, String information, @Size(min = 1) @NotEmpty() int[] category, int brandId, CreateProductOptionDTO[] options) {
        super(name, description, information, category, brandId);
        this.options = options;
    }
}

