package com.kth.kthtechshop.dto.user;

import com.kth.kthtechshop.decorators.address.ValidCustomerAddress;
import com.kth.kthtechshop.decorators.phone_number.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUserDTO {
    @NotBlank(message = "User name should not be empty")
    private String user_name;
    @NotBlank(message = "Last name should not be empty")
    private String last_name;
    @NotBlank(message = "First name should not be empty")
    private String first_name;
    private Boolean gender;
    @ValidPhoneNumber()
    private String phone_number;
    //    @ValidCustomerAddress()
    private String address;
    @Size(min = 6, max = 12, message = "Address should be between 6 and 12 characters")
    private String password;
    @NotEmpty(message = "Email is required")
    private String email;
}
