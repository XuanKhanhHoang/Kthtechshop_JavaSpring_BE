package com.kth.kthtechshop.models;


import com.google.api.client.util.DateTime;
import com.kth.kthtechshop.enums.OrderStatus;
import com.kth.kthtechshop.enums.PaymentMethod;
import com.kth.kthtechshop.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderListProduct> orderProductList;

}
