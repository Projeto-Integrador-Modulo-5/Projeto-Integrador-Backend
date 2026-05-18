package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileUploadServiceTest {

    @TempDir
    Path tempDir;

    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService();
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileUploadService, "baseUrl", "http://localhost:8080");
    }

    // ── store — sucesso ───────────────────────────────────────────────────────

    @Test
    void store_shouldSaveFileAndReturnUrl() throws IOException {
        byte[] content = "fake image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content);

        String url = fileUploadService.store(file);

        assertThat(url).startsWith("http://localhost:8080/uploads/");
        assertThat(url).endsWith(".jpg");
    }

    @Test
    void store_shouldAcceptPngFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "image.png", "image/png", "png content".getBytes());

        String url = fileUploadService.store(file);

        assertThat(url).endsWith(".png");
    }

    @Test
    void store_shouldAcceptWebpFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "image.webp", "image/webp", "webp content".getBytes());

        String url = fileUploadService.store(file);

        assertThat(url).endsWith(".webp");
    }

    // ── store — falhas de validação ───────────────────────────────────────────

    @Test
    void store_shouldThrowWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> fileUploadService.store(emptyFile))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("vazio");
    }

    @Test
    void store_shouldThrowWhenFileExceedsMaxSize() {
        // Gera conteúdo maior que 5 MB
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile(
            "file", "large.jpg", "image/jpeg", largeContent);

        assertThatThrownBy(() -> fileUploadService.store(largeFile))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("5 MB");
    }

    @Test
    void store_shouldThrowWhenContentTypeNotAllowed() {
        MockMultipartFile pdfFile = new MockMultipartFile(
            "file", "doc.pdf", "application/pdf", "pdf content".getBytes());

        assertThatThrownBy(() -> fileUploadService.store(pdfFile))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("não permitido");
    }

    @Test
    void store_shouldThrowWhenContentTypeIsNull() {
        MockMultipartFile noTypeFile = new MockMultipartFile(
            "file", "unknown.bin", null, "binary content".getBytes());

        assertThatThrownBy(() -> fileUploadService.store(noTypeFile))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("não permitido");
    }
}
