package com.kth.kthtechshop.dto.auth.login;

import com.kth.kthtechshop.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class UserGeneralDetailsDTO {
    private Long userId;
    private String firstName;
    private String avatar;
    private Set<Role> roles;
}
