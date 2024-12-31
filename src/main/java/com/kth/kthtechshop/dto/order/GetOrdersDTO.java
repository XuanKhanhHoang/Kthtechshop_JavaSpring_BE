package com.kth.kthtechshop.dto.order;

import com.kth.kthtechshop.dto.GetListCanBeSortDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetOrdersDTO extends GetListCanBeSortDTO {
    private Long user_id;
}
