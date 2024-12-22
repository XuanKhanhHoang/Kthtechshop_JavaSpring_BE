package com.kth.kthtechshop.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String image;
    private Long sellPrice;
    private Integer amount;
    private Integer discount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product products;
}
