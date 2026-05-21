package com.projeto.integrador.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serve arquivos do diretório de upload como recursos estáticos em /uploads/**
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Garante barra no final para o ResourceHandler funcionar corretamente
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
