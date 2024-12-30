package com.kth.kthtechshop.models;

import jakarta.persistence.*;

@Entity
public class OrderListProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;
}

