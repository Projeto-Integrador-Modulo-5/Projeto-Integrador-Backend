package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.auth.AuthResponse;
import com.projeto.integrador.backend.dto.auth.LoginRequest;
import com.projeto.integrador.backend.dto.auth.RegisterRequest;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.exception.UnauthorizedException;
import com.projeto.integrador.backend.repository.UserRepository;
import com.projeto.integrador.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test User", "test@test.com", "encoded_pass", Role.CUSTOMER);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    void register_shouldCreateUserAndReturnTokens() {
        RegisterRequest request = new RegisterRequest("Test User", "test@test.com", "password123");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt_token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.type()).isEqualTo("Bearer");
        verify(userRepository).save(any(User.class));
        verify(valueOps).set(startsWith("refresh:"), anyString(), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Test", "existing@test.com", "password123");
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Email já cadastrado");
    }

    // ── login ────────────────────────────────────────────────────────────────

    @Test
    void login_shouldReturnTokensForValidCredentials() {
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtTokenProvider.generateToken(user)).thenReturn("jwt_token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.refreshToken()).isNotBlank();
        verify(valueOps).set(startsWith("refresh:"), anyString(), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    // ── refresh ──────────────────────────────────────────────────────────────

    @Test
    void refresh_shouldReturnNewTokensAndRotateRefreshToken() {
        String oldRefreshToken = UUID.randomUUID().toString();
        UUID userId = UUID.randomUUID();

        when(valueOps.get("refresh:" + oldRefreshToken)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user)).thenReturn("new_access_token");

        AuthResponse response = authService.refresh(oldRefreshToken);

        assertThat(response.token()).isEqualTo("new_access_token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotEqualTo(oldRefreshToken); // rotacionado
        verify(redisTemplate).delete("refresh:" + oldRefreshToken);
        verify(valueOps, atLeastOnce()).set(startsWith("refresh:"), anyString(), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void refresh_shouldThrowWhenRefreshTokenInvalid() {
        when(valueOps.get(anyString())).thenReturn(null);

        assertThatThrownBy(() -> authService.refresh("token_invalido"))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("inválido ou expirado");
    }

    @Test
    void refresh_shouldThrowWhenUserNotFound() {
        String token = UUID.randomUUID().toString();
        UUID userId = UUID.randomUUID();

        when(valueOps.get("refresh:" + token)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(token))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("Usuário não encontrado");
    }

    // ── logout ───────────────────────────────────────────────────────────────

    @Test
    void logout_shouldBlacklistAccessTokenAndDeleteRefreshToken() {
        when(jwtTokenProvider.getExpiration()).thenReturn(86400000L);

        authService.logout("my_access_token", "my_refresh_token");

        verify(valueOps).set(eq("blacklist:my_access_token"), eq("1"), eq(86400000L), eq(TimeUnit.MILLISECONDS));
        verify(redisTemplate).delete("refresh:my_refresh_token");
    }

    @Test
    void logout_shouldBlacklistAccessTokenEvenWithNullRefreshToken() {
        when(jwtTokenProvider.getExpiration()).thenReturn(86400000L);

        authService.logout("my_access_token", null);

        verify(valueOps).set(eq("blacklist:my_access_token"), eq("1"), eq(86400000L), eq(TimeUnit.MILLISECONDS));
        verify(redisTemplate, never()).delete(anyString());
    }
}
