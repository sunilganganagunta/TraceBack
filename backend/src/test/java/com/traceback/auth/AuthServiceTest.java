package com.traceback.auth;

import com.traceback.auth.dto.AuthResponse;
import com.traceback.auth.dto.LoginRequest;
import com.traceback.auth.dto.RegisterRequest;
import com.traceback.common.ConflictException;
import com.traceback.security.JwtTokenProvider;
import com.traceback.security.UserPrincipal;
import com.traceback.users.AccountStatus;
import com.traceback.users.Role;
import com.traceback.users.User;
import com.traceback.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, tokenProvider);
    }

    @Test
    @DisplayName("Should successfully register a new user with normalized email and encoded password")
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("David Miller", "David@Example.Com", "SecurePass123!");

        when(userRepository.existsByNormalizedEmail("david@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("hashed-secret");

        User savedUser = new User("David Miller", "David@Example.Com", "david@example.com", "hashed-secret", Set.of(Role.ROLE_USER));
        savedUser.setId("user-david");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("David Miller", response.getUser().getName());
        assertEquals("David@Example.Com", response.getUser().getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject registration when normalized email already exists (ConflictException)")
    void testRegister_DuplicateEmail() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "Password123!");
        when(userRepository.existsByNormalizedEmail("john@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
