package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.user.GetUserResponseDTO;
import com.kth.kthtechshop.dto.user.UpdateUserDTO;
import com.kth.kthtechshop.services.GoogleDriveService;
import com.kth.kthtechshop.services.UserService;
import com.kth.kthtechshop.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequestMapping("/api/v1/user/private")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, GoogleDriveService googleDriveService) {
        this.userService = userService;
    }

    @PostMapping("update")
    @Async
    public ResponseEntity<String> update(@RequestBody UpdateUserDTO user, @RequestParam("avatar") MultipartFile avatar) {
        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".png") || originalFilename.endsWith(".jpg") || originalFilename.endsWith(".webp"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only PNG, JPG, and WEBP are allowed.");
        }
        Long userId = SecurityUtil.getUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        userService.update(userId, user);
        if (avatar.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body("User detail full updated successfully");
        try {
            userService.uploadAvatar(userId, avatar);
            return ResponseEntity.status(HttpStatus.OK).body("User detail full updated successfully");
        } catch (IOException | GeneralSecurityException e) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("User detail updated successfully but error in upload image");
        }
    }

    @GetMapping("get_detail")
    public GetUserResponseDTO getUserDetail() {
        Long userId = SecurityUtil.getUserId();
        return userService.getUserDetail(userId);
    }
}
