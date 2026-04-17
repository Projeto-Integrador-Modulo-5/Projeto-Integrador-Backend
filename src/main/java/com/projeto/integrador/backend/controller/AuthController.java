package com.projeto.integrador.backend.controller;

import com.projeto.integrador.backend.dto.auth.AuthResponse;
import com.projeto.integrador.backend.dto.auth.LoginRequest;
import com.projeto.integrador.backend.dto.auth.LogoutRequest;
import com.projeto.integrador.backend.dto.auth.RefreshTokenRequest;
import com.projeto.integrador.backend.dto.auth.RegisterRequest;
import com.projeto.integrador.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar novo usuário")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Login — retorna accessToken + refreshToken")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Renovar access token usando refresh token",
               description = "O refresh token é rotacionado: o enviado é invalidado e um novo é retornado.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(summary = "Logout — invalida access token e (opcionalmente) refresh token",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) LogoutRequest body) {

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String accessToken  = authHeader.substring(7);
            String refreshToken = (body != null) ? body.refreshToken() : null;
            authService.logout(accessToken, refreshToken);
        }
        return ResponseEntity.noContent().build();
    }
}
