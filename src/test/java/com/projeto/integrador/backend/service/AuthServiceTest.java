package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.auth.AuthResponse;
import com.projeto.integrador.backend.dto.auth.LoginRequest;
import com.projeto.integrador.backend.dto.auth.RegisterRequest;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.repository.UserRepository;
import com.projeto.integrador.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test User", "test@test.com", "encoded_pass", Role.CUSTOMER);
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("Test User", "test@test.com", "password123");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt_token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.email()).isEqualTo("test@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Test", "existing@test.com", "password123");
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void login_shouldReturnTokenForValidCredentials() {
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtTokenProvider.generateToken(user)).thenReturn("jwt_token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt_token");
    }
}
