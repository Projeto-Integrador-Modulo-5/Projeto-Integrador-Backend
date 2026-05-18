package com.projeto.integrador.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.integrador.backend.domain.entity.Product;
import com.projeto.integrador.backend.domain.enums.Size;
import com.projeto.integrador.backend.dto.cart.CartItemRequest;
import com.projeto.integrador.backend.dto.cart.CartResponse;
import com.projeto.integrador.backend.dto.cart.UpdateCartItemRequest;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ValueOperations<String, String> valueOps;

    private CartService cartService;

    private UUID userId;
    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        // Usa ObjectMapper real para serialização verdadeira
        cartService = new CartService(redisTemplate, new ObjectMapper(), productRepository);

        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        product = new Product();
        product.setName("Camiseta Test");
        product.setPrice(BigDecimal.valueOf(59.90));
        product.setSizes(new ArrayList<>());
    }

    // ── getCart ───────────────────────────────────────────────────────────────

    @Test
    void getCart_shouldReturnEmptyCartWhenNothingInRedis() {
        when(valueOps.get("cart:" + userId)).thenReturn(null);

        CartResponse result = cartService.getCart(userId);

        assertThat(result.items()).isEmpty();
        assertThat(result.total()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ── addItem ───────────────────────────────────────────────────────────────

    @Test
    void addItem_shouldAddNewItemToEmptyCart() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(valueOps.get("cart:" + userId)).thenReturn(null);

        CartItemRequest request = new CartItemRequest(productId, Size.M, 2);
        CartResponse result = cartService.addItem(userId, request);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).productName()).isEqualTo("Camiseta Test");
        assertThat(result.items().get(0).quantity()).isEqualTo(2);
        assertThat(result.total()).isEqualByComparingTo(BigDecimal.valueOf(119.80));
        verify(valueOps).set(eq("cart:" + userId), anyString(), anyLong(), any());
    }

    @Test
    void addItem_shouldIncrementQuantityForExistingItem() throws Exception {
        // Prepara carrinho com 1 item já existente
        CartItemData existing = new CartItemData(productId, "Camiseta Test", "M", 1, BigDecimal.valueOf(59.90));
        String cartJson = new ObjectMapper().writeValueAsString(java.util.List.of(existing));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(valueOps.get("cart:" + userId)).thenReturn(cartJson);

        CartItemRequest request = new CartItemRequest(productId, Size.M, 3);
        CartResponse result = cartService.addItem(userId, request);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).quantity()).isEqualTo(4); // 1 + 3
    }

    @Test
    void addItem_shouldThrowWhenProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        CartItemRequest request = new CartItemRequest(productId, Size.G, 1);
        assertThatThrownBy(() -> cartService.addItem(userId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateItem ────────────────────────────────────────────────────────────

    @Test
    void updateItem_shouldUpdateQuantityForExistingItem() throws Exception {
        CartItemData existing = new CartItemData(productId, "Camiseta Test", "M", 2, BigDecimal.valueOf(59.90));
        String cartJson = new ObjectMapper().writeValueAsString(java.util.List.of(existing));

        when(valueOps.get("cart:" + userId)).thenReturn(cartJson);

        UpdateCartItemRequest request = new UpdateCartItemRequest(Size.M, 5);
        CartResponse result = cartService.updateItem(userId, productId, request);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).quantity()).isEqualTo(5);
        assertThat(result.total()).isEqualByComparingTo(BigDecimal.valueOf(299.50));
    }

    @Test
    void updateItem_shouldThrowWhenItemNotFoundInCart() {
        when(valueOps.get("cart:" + userId)).thenReturn(null);

        UpdateCartItemRequest request = new UpdateCartItemRequest(Size.M, 1);
        assertThatThrownBy(() -> cartService.updateItem(userId, productId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item não encontrado");
    }

    // ── removeItem ────────────────────────────────────────────────────────────

    @Test
    void removeItem_shouldRemoveItemFromCart() throws Exception {
        CartItemData existing = new CartItemData(productId, "Camiseta Test", "M", 1, BigDecimal.valueOf(59.90));
        String cartJson = new ObjectMapper().writeValueAsString(java.util.List.of(existing));

        when(valueOps.get("cart:" + userId)).thenReturn(cartJson);

        CartResponse result = cartService.removeItem(userId, productId, cartJson);

        assertThat(result.items()).isEmpty();
        assertThat(result.total()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ── clearCart ─────────────────────────────────────────────────────────────

    @Test
    void clearCart_shouldDeleteCartKeyFromRedis() {
        cartService.clearCart(userId);

        verify(redisTemplate).delete("cart:" + userId);
    }
}
