package com.kth.kthtechshop.controllers;

import com.kth.kthtechshop.dto.auth.login.LoginDTO;
import com.kth.kthtechshop.dto.auth.login.LoginResponseDTO;
import com.kth.kthtechshop.dto.auth.register.RegisterDTO;
import com.kth.kthtechshop.dto.auth.register.RegisterResponseDTO;
import com.kth.kthtechshop.exception.BadRequestException;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
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
    public CompletableFuture<RegisterResponseDTO> register(@Valid @RequestBody
                                                           RegisterDTO userDto) {
        return authService.register(userDto);
    }

    @GetMapping("verify")
    public RedirectView verifyUser(@RequestParam("token") String token) {
        if (token == null || token.trim().isEmpty()) throw new BadRequestException();
        authService.verify(token);
        return new RedirectView("http://localhost:3000/auth/active_success");
    }

    @PostMapping("login_by_facebook")
    @Async
    public LoginResponseDTO loginByFacebook(@RequestParam("access_token") String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString("https://graph.facebook.com/me").queryParam("fields", "id,name,email").queryParam("access_token", accessToken).toUriString();
        Map<String, Object> facebookUser = restTemplate.getForObject(url, Map.class);
        if (facebookUser == null || !facebookUser.containsKey("id") || !facebookUser.containsKey("email")) {
            throw new NotFoundException("Can't find Facebook account from access_token");
        }
        String email = (String) facebookUser.get("email");
        String facebookId = (String) facebookUser.get("id");
        return authService.loginByFacebook(email, facebookId);
    }
}
