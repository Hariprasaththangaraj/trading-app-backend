package com.trading.app.login.service.impl;

import com.trading.app.login.model.SignupRequest;
import com.trading.app.login.model.SignupResponse;
import com.trading.app.login.model.User;
import com.trading.app.login.repo.UserRepository;
import com.trading.app.login.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already registered");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encryptedPassword);
        user.setAuthProvider("LOCAL");
        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public SignupResponse login(SignupRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new RuntimeException("Invalid username or password");
        }
        String token = jwtService.generateToken(user.getUsername());
        SignupResponse response = new SignupResponse();
        response.setToken(token);
        response.setMessage("Logged in");
        return response;
    }
}