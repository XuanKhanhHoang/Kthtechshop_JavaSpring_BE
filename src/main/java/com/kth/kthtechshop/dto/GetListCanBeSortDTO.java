package com.kth.kthtechshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListCanBeSortDTO {
    @Min(1)
    private Integer page = 1;
    @Min(1)
    private Integer limit = 6;
    @Pattern(regexp = "ASC|DESC")
    private String order_type;
    @Size(max = 255)
    private String order_col;
}
