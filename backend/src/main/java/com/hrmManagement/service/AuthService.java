package com.hrmManagement.service;

import com.hrmManagement.config.JwtProperties;
import com.hrmManagement.dto.LoginRequest;
import com.hrmManagement.dto.LoginResponse;
import com.hrmManagement.entity.User;
import com.hrmManagement.repository.EmployeeRepository;
import com.hrmManagement.repository.UserRepository;
import com.hrmManagement.security.CustomUserDetails;
import com.hrmManagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailFetchingRolesAndPermissions(request.email())
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        if (!user.isActive()) {
            throw new DisabledException("This account is disabled");
        }

        user.setLastLoginAt(LocalDateTime.now());

        CustomUserDetails principal = CustomUserDetails.fromUser(user,
                employeeRepository.findByUserId(user.getId()).orElse(null));
        String token = jwtService.generateToken(principal);

        return new LoginResponse(
                token,
                "Bearer",
                jwtProperties.expirationMs() / 1000,
                user.getFullName(),
                user.getUserType().name()
        );
    }
}
