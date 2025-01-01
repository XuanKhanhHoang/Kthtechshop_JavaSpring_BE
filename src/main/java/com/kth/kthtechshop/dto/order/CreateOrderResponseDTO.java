package com.kth.kthtechshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateOrderResponseDTO {
    String payment_url;
    String message;
    Long order_id;
}
