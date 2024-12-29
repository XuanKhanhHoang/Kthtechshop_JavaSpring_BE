package com.kth.kthtechshop.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDTO {
    private JWTToken accessToken;
    private JWTToken refreshToken;
    private UserGeneralDetailsDTO userInfo;
}
