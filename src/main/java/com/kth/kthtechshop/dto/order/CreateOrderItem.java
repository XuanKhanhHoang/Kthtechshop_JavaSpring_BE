package com.kth.kthtechshop.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateOrderItem {
    @NotNull(message = "Option ID cannot be null.")
    @Min(0)
    private Long optionId;
    @NotNull(message = "Amount cannot be null.")
    @Min(1)
    private Integer amount;
}
