package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.auth.login.LoginResponseDTO;
import com.kth.kthtechshop.dto.auth.login.UserGeneralDetailsDTO;
import com.kth.kthtechshop.exception.UnauthorizedException;
import com.kth.kthtechshop.models.User;
import com.kth.kthtechshop.repository.UserRepository;
import com.kth.kthtechshop.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Async
    public CompletableFuture<LoginResponseDTO> Login(String userName, String password) {
        User user = userRepository.findByUserName(userName);
        if (user == null || !user.getIsAuth() || !user.getIsValid()) throw new UnauthorizedException();
        boolean valid = PasswordUtil.checkPassword(password, user.getPassword());
        if (!valid) throw new UnauthorizedException();
        return CompletableFuture.completedFuture(new LoginResponseDTO(jwtUtil.generateToken(user.getId(), user.getRoles()), new UserGeneralDetailsDTO(user.getId(), user.getFirstName(), user.getAvatar(), user.getRoles())));
    }
}
