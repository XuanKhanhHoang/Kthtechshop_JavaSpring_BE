package com.kth.kthtechshop.models;


import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime receivedAt;
    private String delivery_address;
    private Integer delivery_fee;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.Cash;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.WaitingForDelivering;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderListProduct> orderProductList;

    public Order(Integer delivery_fee, String delivery_address, PaymentMethod paymentMethod, User user) {
        this.delivery_fee = delivery_fee;
        this.delivery_address = delivery_address;
        this.paymentMethod = paymentMethod;
        this.user = user;
    }

    public Order(Integer delivery_fee, String delivery_address, User user) {
        this.delivery_fee = delivery_fee;
        this.delivery_address = delivery_address;
        this.user = user;
    }
}
