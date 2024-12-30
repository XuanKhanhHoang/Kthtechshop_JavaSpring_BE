package com.kth.kthtechshop.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MainProductDetailDTO {
    @NotBlank
    private String name;
    private String description;
    private String information;
    @Size(min = 1)
    @NotEmpty()
    private int[] category;
    @NotNull(message = "Brand ID cannot be null.")
    @Min(value = 1, message = "Brand ID must be a positive number.")
    private int brandId;
}
