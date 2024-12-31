package com.kth.kthtechshop.dto.order;

import com.kth.kthtechshop.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateOrderStatusDTO {
    @NotNull
    @Min(0)
    private Long order_id;
    @Min(0)
    @NotNull
    private int status_id;
}
