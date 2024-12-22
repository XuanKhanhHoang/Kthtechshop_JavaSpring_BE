package com.kth.kthtechshop.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class LoginResponseDTO {
    private AccessToken accessToken;
    private UserGeneralDetailsDTO userInfo;
}
