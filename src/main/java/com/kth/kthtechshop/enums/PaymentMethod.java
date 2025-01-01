package com.kth.kthtechshop.enums;

import jakarta.validation.constraints.NotNull;

public enum PaymentMethod {
    Cash,
    VNpay;

    public static PaymentMethod getPaymentMethod(@NotNull(message = "Payment method ID cannot be null.") Integer paymentMethodId) {
        for (var payment : PaymentMethod.values()) {
            if (payment.ordinal() == paymentMethodId) {
                return payment;
            }
        }
        return null;
    }
}
