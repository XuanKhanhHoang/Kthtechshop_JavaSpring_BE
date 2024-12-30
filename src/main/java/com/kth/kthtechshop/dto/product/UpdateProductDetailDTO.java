package com.kth.kthtechshop.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductDetailDTO extends MainProductDetailDTO {
    @Min(0)
    private Long id;

    public UpdateProductDetailDTO(String name, String description, String information, @Size(min = 1) @NotEmpty() int[] category, int brandId, Long id) {
        super(name, description, information, category, brandId);
        this.id = id;
    }
}
