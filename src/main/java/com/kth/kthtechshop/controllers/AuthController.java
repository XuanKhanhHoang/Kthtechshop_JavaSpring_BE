package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.ListResponse;
import com.kth.kthtechshop.dto.auth.login.LoginDTO;
import com.kth.kthtechshop.dto.auth.login.LoginResponseDTO;
import com.kth.kthtechshop.dto.auth.register.RegisterResponseDTO;
import com.kth.kthtechshop.models.Category;
import com.kth.kthtechshop.services.AuthService;
import com.kth.kthtechshop.services.CommonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public CompletableFuture<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.Login(loginDTO.getUser_name(), loginDTO.getPassword());
    }

    @PostMapping("register")
    public  CompletableFuture<RegisterResponseDTO>register(){
        return
    }
}
