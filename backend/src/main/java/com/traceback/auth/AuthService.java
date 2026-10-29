package com.traceback.auth;

import com.traceback.auth.dto.AuthResponse;
import com.traceback.auth.dto.LoginRequest;
import com.traceback.auth.dto.RegisterRequest;
import com.traceback.common.ConflictException;
import com.traceback.common.ResourceNotFoundException;
import com.traceback.security.JwtTokenProvider;
import com.traceback.security.UserPrincipal;
import com.traceback.users.Role;
import com.traceback.users.User;
import com.traceback.users.UserRepository;
import com.traceback.users.dto.UserDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByNormalizedEmail(normalizedEmail)) {
            throw new ConflictException("Email address is already registered: " + request.getEmail());
        }

        User user = new User(
                request.getName().trim(),
                request.getEmail().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                Set.of(Role.ROLE_USER)
        );
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setLastLoginAt(Instant.now());

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, UserDto.from(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByNormalizedEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, UserDto.from(user));
    }

    public UserDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + principal.getId()));
        return UserDto.from(user);
    }
}
