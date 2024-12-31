package com.kth.kthtechshop.dto.order;

import com.kth.kthtechshop.dto.user.GetUserResponseDTO;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.enums.PaymentMethod;
import com.kth.kthtechshop.models.OrderListProduct;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private LocalDateTime createAt;
    private LocalDateTime receivedAt;
    private String delivery_address;
    private Integer delivery_fee;

    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private GetUserResponseDTO user;
    private List<OrderListProductItemDTO> orderProductList;

    public OrderDTO(Long id, GetUserResponseDTO user, OrderStatus status, PaymentMethod paymentMethod, Integer delivery_fee, String delivery_address, LocalDateTime receivedAt, LocalDateTime createAt, List<OrderListProductItemDTO> orderProductList) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.delivery_fee = delivery_fee;
        this.delivery_address = delivery_address;
        this.receivedAt = receivedAt;
        this.createAt = createAt;
        this.orderProductList = orderProductList;
    }
}
