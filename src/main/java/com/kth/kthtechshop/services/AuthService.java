package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.auth.login.LoginResponseDTO;
import com.kth.kthtechshop.dto.auth.login.UserGeneralDetailsDTO;
import com.kth.kthtechshop.dto.auth.register.RegisterDTO;
import com.kth.kthtechshop.dto.auth.register.RegisterResponseDTO;
import com.kth.kthtechshop.exception.NotFoundException;
import com.kth.kthtechshop.exception.UnauthorizedException;
import com.kth.kthtechshop.models.User;
import com.kth.kthtechshop.repository.UserRepository;
import com.kth.kthtechshop.utils.PasswordUtil;
import com.kth.kthtechshop.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Async
    public CompletableFuture<LoginResponseDTO> Login(String userName, String password) {
        User user = userRepository.findByUserName(userName);
        if (user == null || !user.getIsAuth() || !user.getIsValid()) throw new UnauthorizedException();
        boolean valid = PasswordUtil.checkPassword(password, user.getPassword());
        if (!valid) throw new UnauthorizedException();
        return CompletableFuture.completedFuture(new LoginResponseDTO(jwtUtil.generateToken(user.getId(), user.getRoles()), jwtUtil.generateRefreshToken(user.getId(), user.getRoles()), new UserGeneralDetailsDTO(user.getId(), user.getFirstName(), user.getAvatar(), user.getRoles())));
    }

    public CompletableFuture<RegisterResponseDTO> register(RegisterDTO userDto) {
        User newUser = new User(userDto);
        try {
            var res = userRepository.save(newUser);
            RegisterResponseDTO response = new RegisterResponseDTO(String.format("User with id %d is created, please check your email to activate your account", res.getId()), 201);
            String token = jwtUtil.generateToken(newUser.getId(), 15, newUser.getRoles()).getToken();
            emailService.sendEmail(res.getEmail(), "Active your account", String.format("Your active link is: <a href='http://localhost:8081/api/v1/auth/active?token=%s'>Click here to active your account</a>", token));
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new RegisterResponseDTO("Registration failed: " + e.getMessage(), 500));
        }
    }


    public void verify(String token) {
        long userId = -1;
        try {
            userId = jwtUtil.validateToken(token);
        } catch (RuntimeException e) {
            throw new UnauthorizedException();
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) throw new UnauthorizedException();
        User updatedUser = user.get();
        updatedUser.setIsAuth(true);
        userRepository.save(updatedUser);
    }

    public LoginResponseDTO loginByFacebook(String email, String facebookId) {
        Optional<User> userCon = userRepository.findByEmailAndFacebookId(email, facebookId);
        if (userCon.isEmpty()) throw new UnauthorizedException("User isn't exist.");
        User user = userCon.get();
        return new LoginResponseDTO(jwtUtil.generateToken(user.getId(), user.getRoles()), jwtUtil.generateRefreshToken(user.getId(), user.getRoles()), new UserGeneralDetailsDTO(user.getId(), user.getFirstName(), user.getAvatar(), user.getRoles()));
    }

    @Async
    public ResponseEntity<String> passwordRecovery(String email) {
        var user = userRepository.findByEmail(email);
        if (user == null) throw new NotFoundException();
        String newPwd = RandomStringGenerator.generateRandomString();
        emailService.sendEmail(email, "Active your account", "Your new password is: " + newPwd);
        user.setPassword(PasswordUtil.hashPassword(newPwd));
        userRepository.save(user);
        return ResponseEntity.ok("password is updated");
    }
}
