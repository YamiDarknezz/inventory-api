package com.darkhub.api.inventory.service;

import com.darkhub.api.inventory.dto.AuthResponse;
import com.darkhub.api.inventory.dto.LoginRequest;
import com.darkhub.api.inventory.dto.RegisterRequest;
import com.darkhub.api.inventory.dto.UserResponse;
import com.darkhub.api.inventory.exception.DuplicateException;
import com.darkhub.api.inventory.model.Role;
import com.darkhub.api.inventory.model.User;
import com.darkhub.api.inventory.repository.UserRepository;
import com.darkhub.api.inventory.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateException("Email already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer", UserResponse.from(user));
    }
}