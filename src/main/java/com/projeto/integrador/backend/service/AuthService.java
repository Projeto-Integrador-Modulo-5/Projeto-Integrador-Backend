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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    /** TTL do refresh token: 7 dias em milissegundos */
    private static final long REFRESH_TOKEN_TTL_MS = 7L * 24 * 60 * 60 * 1000;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
                       StringRedisTemplate redisTemplate) {
        this.userRepository       = userRepository;
        this.passwordEncoder      = passwordEncoder;
        this.jwtTokenProvider     = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.redisTemplate        = redisTemplate;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado: " + request.email());
        }

        User user = new User(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password()),
            Role.CUSTOMER
        );
        user = userRepository.save(user);

        String accessToken  = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user.getId().toString());
        return new AuthResponse(accessToken, refreshToken,
                user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken  = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user.getId().toString());
        return new AuthResponse(accessToken, refreshToken,
                user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }

    /**
     * Gera novo access token a partir de um refresh token válido.
     * O refresh token é rotacionado a cada chamada (refresh token rotation).
     */
    public AuthResponse refresh(String refreshToken) {
        String redisKey = "refresh:" + refreshToken;
        String userId   = redisTemplate.opsForValue().get(redisKey);

        if (userId == null) {
            throw new UnauthorizedException("Refresh token inválido ou expirado");
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        // Rotaciona: apaga o antigo e gera um novo
        redisTemplate.delete(redisKey);
        String newRefreshToken = createRefreshToken(userId);
        String newAccessToken  = jwtTokenProvider.generateToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken,
                user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }

    /**
     * Invalida access token (blacklist) e, se fornecido, apaga o refresh token.
     */
    public void logout(String accessToken, String refreshToken) {
        long ttl = jwtTokenProvider.getExpiration();
        redisTemplate.opsForValue().set("blacklist:" + accessToken, "1", ttl, TimeUnit.MILLISECONDS);

        if (refreshToken != null && !refreshToken.isBlank()) {
            redisTemplate.delete("refresh:" + refreshToken);
        }
    }

    // ── privado ──────────────────────────────────────────────────────────────

    private String createRefreshToken(String userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh:" + token, userId, REFRESH_TOKEN_TTL_MS, TimeUnit.MILLISECONDS);
        return token;
    }
}
