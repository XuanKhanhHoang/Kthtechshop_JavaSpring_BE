package com.kth.kthtechshop.dto.product;

import com.kth.kthtechshop.dto.GetListCanBeSortDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProductsDTO extends GetListCanBeSortDTO {
    @Min(0)
    private Integer brand_id;
    @Size(max = 255)
    private String key_word;
    @Min(0)
    private Long min_price;
    @Min(0)
    private Long max_price;
    private Double rating;
    private List<Long> category_ids;
}
