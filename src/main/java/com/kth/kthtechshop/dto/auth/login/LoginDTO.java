package com.kth.kthtechshop.dto.auth.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginDTO {
    @NotEmpty
    private String user_name;
    @NotEmpty
    private String password;
}
