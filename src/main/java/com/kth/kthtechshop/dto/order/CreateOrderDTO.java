package com.kth.kthtechshop.dto.order;

import com.kth.kthtechshop.decorators.address.ValidCustomerAddress;
import com.kth.kthtechshop.decorators.phone_number.ValidPhoneNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CreateOrderDTO {
    @NotNull
    @Size(min = 1, message = "Data list cannot be empty.")
    private List<CreateOrderItem> data;
    @NotNull(message = "Payment method ID cannot be null.")
    private Integer paymentMethodId;
    @ValidPhoneNumber
    private String phoneNumber;
    //  @ValidCustomerAddress
    private String address;
}
