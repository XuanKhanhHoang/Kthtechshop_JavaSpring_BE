package com.kth.kthtechshop.dto.order;

import com.kth.kthtechshop.dto.GetListCanBeSortDTO;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetOrdersDTO extends GetListCanBeSortDTO {
    @Min(1)
    private Long user_id;
    @Min(1)
    private Integer statusId;
}
