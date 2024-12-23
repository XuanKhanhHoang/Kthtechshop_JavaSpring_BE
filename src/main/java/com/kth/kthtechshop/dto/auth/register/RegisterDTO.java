package com.kth.kthtechshop.dto.auth.register;

import com.kth.kthtechshop.decorators.address.ValidCustomerAddress;
import com.kth.kthtechshop.decorators.email.ValidEmail;
import com.kth.kthtechshop.decorators.phone_number.ValidPhoneNumber;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterDTO {
    @NotEmpty(message = "Username is required")
    private String user_name;
    @NotEmpty(message = "Password is required")
    private String password;
    @NotEmpty(message = "Email is required")
    @ValidEmail()
    private String email;
    @NotEmpty(message = "Phone number is required")
    @ValidPhoneNumber()
    private String phone_number;
    @NotEmpty(message = "First name is required")
    private String first_name;
    @NotEmpty(message = "Last name is required")
    private String last_name;
    //    @ValidCustomerAddress()
    @NotEmpty(message = "Address is required")
    private String address;
    private Boolean gender;
}
