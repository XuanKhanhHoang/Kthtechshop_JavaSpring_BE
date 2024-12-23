package com.kth.kthtechshop.dto.auth.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponseDTO {
    private String message;
    private int status;
}
