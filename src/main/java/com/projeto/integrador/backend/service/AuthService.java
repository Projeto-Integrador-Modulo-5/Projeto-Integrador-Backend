package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.User;
import com.projeto.integrador.backend.domain.enums.Role;
import com.projeto.integrador.backend.dto.auth.AuthResponse;
import com.projeto.integrador.backend.dto.auth.LoginRequest;
import com.projeto.integrador.backend.dto.auth.RegisterRequest;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.repository.UserRepository;
import com.projeto.integrador.backend.security.JwtTokenProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
                       StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
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

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }

    public void logout(String token) {
        long expiration = jwtTokenProvider.getExpiration();
        redisTemplate.opsForValue().set("blacklist:" + token, "1", expiration, TimeUnit.MILLISECONDS);
    }
}
