package com.kth.kthtechshop.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccessToken {
    private String token;
    private long iat;
    private long exp;
}