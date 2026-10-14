package com.sassmodel.service;

import com.sassmodel.config.JwtProperties;
import com.sassmodel.dto.LoginRequest;
import com.sassmodel.dto.LoginResponse;
import com.sassmodel.entity.User;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.CustomUserDetails;
import com.sassmodel.security.JwtService;
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

        CustomUserDetails principal = CustomUserDetails.fromUser(user);
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
