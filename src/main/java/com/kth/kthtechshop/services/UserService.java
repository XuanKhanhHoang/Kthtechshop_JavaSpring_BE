package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.user.GetUserResponseDTO;
import com.kth.kthtechshop.dto.user.UpdateUserDTO;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.models.User;
import com.kth.kthtechshop.repository.UserRepository;
import com.kth.kthtechshop.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final GoogleDriveService driveService;

    @Autowired
    public UserService(UserRepository userRepository, GoogleDriveService driveService) {
        this.userRepository = userRepository;
        this.driveService = driveService;
    }

    public void update(long userId, UpdateUserDTO updateUserDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (updateUserDTO.getUser_name() != null) {
                user.setUserName(updateUserDTO.getUser_name());
            }
            if (updateUserDTO.getLast_name() != null) {
                user.setLastName(updateUserDTO.getLast_name());
            }
            if (updateUserDTO.getFirst_name() != null) {
                user.setFirstName(updateUserDTO.getFirst_name());
            }
            if (updateUserDTO.getGender() != null) {
                user.setGender(updateUserDTO.getGender());
            }
            if (updateUserDTO.getPhone_number() != null) {
                user.setPhoneNumber(updateUserDTO.getPhone_number());
            }
            if (updateUserDTO.getAddress() != null) {
                user.setAddress(updateUserDTO.getAddress());
            }
            if (updateUserDTO.getPassword() != null) {
                user.setPassword(PasswordUtil.hashPassword(updateUserDTO.getPassword()));
            }
            if (updateUserDTO.getEmail() != null) {
                user.setEmail(updateUserDTO.getEmail());
            }
            userRepository.save(user);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Async
    public void uploadAvatar(long userId, MultipartFile file) throws GeneralSecurityException, IOException {
        String id = driveService.uploadFile(file, String.format("User%d_Avatar", userId));
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAvatar(id);
            userRepository.save(user);
        }
    }

    public GetUserResponseDTO getUserDetail(Long userId) {
        var user = this.userRepository.findById(userId);
        if (user.isEmpty()) throw new NotFoundException();
        return new GetUserResponseDTO(user.get());
    }
}
