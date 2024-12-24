package com.kth.kthtechshop.models;

import com.kth.kthtechshop.dto.auth.register.RegisterDTO;
import com.kth.kthtechshop.dto.user.UpdateUserDTO;
import com.kth.kthtechshop.enums.Role;
import com.kth.kthtechshop.utils.PasswordUtil;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    private Boolean gender = true;
    private String firstName;
    private String lastName;
    private String userName;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String phoneNumber;
    private String address;
    private String avatar;

    @Builder.Default
    private Boolean isAuth = false;
    @Builder.Default
    private Boolean isValid = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    private String facebookId;

    public User(RegisterDTO userDto) {
        this.gender = userDto.getGender();
        this.firstName = userDto.getLast_name();
        this.lastName = userDto.getLast_name();
        this.userName = userDto.getLast_name();
        this.email = userDto.getEmail();
        this.password = PasswordUtil.hashPassword(userDto.getPassword());
        this.phoneNumber = userDto.getPhone_number();
        this.address = userDto.getAddress();
        this.roles = new HashSet<>();
        this.roles.add(Role.NormalUser);
    }
}
