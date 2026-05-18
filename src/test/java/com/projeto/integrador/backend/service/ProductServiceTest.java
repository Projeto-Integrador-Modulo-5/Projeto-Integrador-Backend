package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Product;
import com.projeto.integrador.backend.dto.PageResponse;
import com.projeto.integrador.backend.dto.product.ProductResponse;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService productService;

    private Product buildProduct(String name, double price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(BigDecimal.valueOf(price));
        p.setActive(true);
        p.setSizes(new ArrayList<>());
        return p;
    }

    // ── getActiveProducts (list) ──────────────────────────────────────────────

    @Test
    void getActiveProducts_shouldReturnOnlyActiveProducts() {
        Product p = buildProduct("Camiseta", 49.90);
        when(productRepository.findByActiveTrue()).thenReturn(List.of(p));

        List<ProductResponse> result = productService.getActiveProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Camiseta");
    }

    @Test
    void getActiveProducts_shouldReturnEmptyListWhenNoActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of());

        List<ProductResponse> result = productService.getActiveProducts();

        assertThat(result).isEmpty();
    }

    // ── getActiveProducts (paginated) ─────────────────────────────────────────

    @Test
    void getActiveProducts_paginated_shouldReturnPageResponse() {
        Product p = buildProduct("Camiseta Polo", 89.90);
        Pageable pageable = PageRequest.of(0, 12, Sort.by("name"));
        Page<Product> page = new PageImpl<>(List.of(p), pageable, 1);

        when(productRepository.findByActiveTrue(pageable)).thenReturn(page);

        PageResponse<ProductResponse> result = productService.getActiveProducts(pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Camiseta Polo");
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.last()).isTrue();
    }

    // ── getProductById ────────────────────────────────────────────────────────

    @Test
    void getProductById_shouldReturnProductWhenFound() {
        UUID id = UUID.randomUUID();
        Product p = buildProduct("Camiseta Basic", 39.90);
        when(productRepository.findById(id)).thenReturn(Optional.of(p));

        ProductResponse result = productService.getProductById(id);

        assertThat(result.name()).isEqualTo("Camiseta Basic");
    }

    @Test
    void getProductById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateProductImage ────────────────────────────────────────────────────

    @Test
    void updateProductImage_shouldUpdateAndReturnProduct() {
        UUID id = UUID.randomUUID();
        Product p = buildProduct("Camiseta", 49.90);
        when(productRepository.findById(id)).thenReturn(Optional.of(p));
        when(productRepository.save(p)).thenReturn(p);

        ProductResponse result = productService.updateProductImage(id, "http://example.com/img.jpg");

        assertThat(result.imageUrl()).isEqualTo("http://example.com/img.jpg");
        verify(productRepository).save(p);
    }

    @Test
    void updateProductImage_shouldThrowWhenProductNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProductImage(id, "http://example.com/img.jpg"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deactivateProduct ─────────────────────────────────────────────────────

    @Test
    void deactivateProduct_shouldSetActiveFalse() {
        UUID id = UUID.randomUUID();
        Product p = buildProduct("Camiseta", 49.90);
        when(productRepository.findById(id)).thenReturn(Optional.of(p));
        when(productRepository.save(p)).thenReturn(p);

        productService.deactivateProduct(id);

        assertThat(p.isActive()).isFalse();
        verify(productRepository).save(p);
    }
}
