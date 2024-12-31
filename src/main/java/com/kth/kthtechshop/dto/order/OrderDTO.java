package com.kth.kthtechshop.dto.order;

import com.kth.kthtechshop.dto.user.GetUserResponseDTO;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.enums.PaymentMethod;
import com.kth.kthtechshop.models.Order;
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
    private List<ProductOptionDetail> orderProductList;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.user = new GetUserResponseDTO(order.getUser());
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.delivery_fee = order.getDelivery_fee();
        this.delivery_address = order.getDelivery_address();
        this.receivedAt = order.getReceivedAt();
        this.createAt = order.getCreateAt();

        this.orderProductList = order.getOrderProductList().stream().map(item -> {
            var tmp = item.getProductOption();
            String productName = tmp.getProducts().getName();
            String productOptionName = tmp.getName();
            int quantity = item.getQuantity();
            Long priceSell = item.getSellingPrice();
            int discount = item.getDiscount();
            return new ProductOptionDetail(productName, productOptionName, quantity, priceSell, discount);
        }).toList();
    }
}
