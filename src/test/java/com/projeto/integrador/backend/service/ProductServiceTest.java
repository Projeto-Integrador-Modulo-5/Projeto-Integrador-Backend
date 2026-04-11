package com.projeto.integrador.backend.service;

import com.projeto.integrador.backend.domain.entity.Product;
import com.projeto.integrador.backend.dto.product.ProductResponse;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService productService;

    @Test
    void getActiveProducts_shouldReturnOnlyActiveProducts() {
        Product p = new Product();
        p.setName("Camiseta");
        p.setPrice(BigDecimal.valueOf(49.90));
        when(productRepository.findByActiveTrue()).thenReturn(List.of(p));

        List<ProductResponse> result = productService.getActiveProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Camiseta");
    }

    @Test
    void getProductById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
