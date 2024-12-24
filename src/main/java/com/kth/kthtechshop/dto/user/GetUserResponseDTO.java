package com.kth.kthtechshop.dto.user;

import com.kth.kthtechshop.models.User;
import lombok.Getter;

@Getter
public class GetUserResponseDTO {
    private Long id;
    private Boolean gender;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private Boolean isAuth;
    private Boolean isValid;

    public GetUserResponseDTO(User user) {
        this.id = user.getId();
        this.gender = user.getGender();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.avatar = user.getAvatar();
        this.isAuth = user.getIsAuth();
        this.isValid = user.getIsValid();
    }
}
