package com.projeto.integrador.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "TeeStore API",
        version     = "1.0",
        description = "API REST do e-commerce TeeStore. Autentique-se em /auth/login e use o accessToken no botão Authorize.",
        contact     = @Contact(name = "Victor Hugo", email = "vhaporfiro@gmail.com")
    ),
    servers = @Server(url = "/", description = "Local"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name        = "bearerAuth",
    type        = SecuritySchemeType.HTTP,
    scheme      = "bearer",
    bearerFormat = "JWT",
    description = "Insira o accessToken retornado pelo /auth/login"
)
public class OpenApiConfig {}
