package com.kth.kthtechshop.dto.order;

import lombok.Getter;

@Getter
public class ProductOptionDetail {
    private final Long price_sell;
    private final String product_name;
    private final String product_option_name;
    private final int quantity;
    private final int discount;

    public ProductOptionDetail(String productName, String productOptionName, int quantity, Long priceSell, int discount) {
        this.product_name = productName;
        this.product_option_name = productOptionName;
        this.quantity = quantity;
        this.price_sell = priceSell;
        this.discount = discount;
    }
}
