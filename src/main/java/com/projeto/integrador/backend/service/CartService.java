package com.projeto.integrador.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.integrador.backend.domain.entity.Product;
import com.projeto.integrador.backend.dto.cart.CartItemRequest;
import com.projeto.integrador.backend.dto.cart.CartItemResponse;
import com.projeto.integrador.backend.dto.cart.CartResponse;
import com.projeto.integrador.backend.dto.cart.UpdateCartItemRequest;
import com.projeto.integrador.backend.exception.BusinessException;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private static final long CART_TTL_DAYS = 7;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    public CartService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper,
                       ProductRepository productRepository) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
    }

    private String cartKey(UUID userId) {
        return "cart:" + userId;
    }

    private List<CartItemData> loadCart(UUID userId) {
        String json = redisTemplate.opsForValue().get(cartKey(userId));
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Erro ao deserializar carrinho", e);
            return new ArrayList<>();
        }
    }

    private void saveCart(UUID userId, List<CartItemData> items) {
        try {
            String json = objectMapper.writeValueAsString(items);
            redisTemplate.opsForValue().set(cartKey(userId), json, CART_TTL_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar carrinho", e);
        }
    }

    public CartResponse getCart(UUID userId) {
        return toCartResponse(loadCart(userId));
    }

    public CartResponse addItem(UUID userId, CartItemRequest request) {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        List<CartItemData> items = loadCart(userId);
        String sizeStr = request.size().name();

        boolean updated = false;
        for (int i = 0; i < items.size(); i++) {
            CartItemData item = items.get(i);
            if (item.productId().equals(request.productId()) && item.size().equals(sizeStr)) {
                items.set(i, new CartItemData(item.productId(), item.productName(), item.size(),
                    item.quantity() + request.quantity(), item.unitPrice()));
                updated = true;
                break;
            }
        }

        if (!updated) {
            items.add(new CartItemData(request.productId(), product.getName(), sizeStr,
                request.quantity(), product.getPrice()));
        }

        saveCart(userId, items);
        return toCartResponse(items);
    }

    public CartResponse updateItem(UUID userId, UUID productId, UpdateCartItemRequest request) {
        List<CartItemData> items = loadCart(userId);
        String sizeStr = request.size().name();

        boolean found = false;
        for (int i = 0; i < items.size(); i++) {
            CartItemData item = items.get(i);
            if (item.productId().equals(productId) && item.size().equals(sizeStr)) {
                items.set(i, new CartItemData(item.productId(), item.productName(), item.size(),
                    request.quantity(), item.unitPrice()));
                found = true;
                break;
            }
        }

        if (!found) throw new ResourceNotFoundException("Item não encontrado no carrinho");

        saveCart(userId, items);
        return toCartResponse(items);
    }

    public CartResponse removeItem(UUID userId, UUID productId) {
        List<CartItemData> items = loadCart(userId);
        items.removeIf(item -> item.productId().equals(productId));
        saveCart(userId, items);
        return toCartResponse(items);
    }

    public void clearCart(UUID userId) {
        redisTemplate.delete(cartKey(userId));
    }

    private CartResponse toCartResponse(List<CartItemData> items) {
        List<CartItemResponse> responses = items.stream()
            .map(item -> new CartItemResponse(
                item.productId(),
                item.productName(),
                item.size(),
                item.quantity(),
                item.unitPrice(),
                item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()))
            ))
            .toList();

        BigDecimal total = responses.stream()
            .map(CartItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(responses, total);
    }
}
