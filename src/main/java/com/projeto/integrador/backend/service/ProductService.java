package com.projeto.integrador.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.integrador.backend.domain.entity.Product;
import com.projeto.integrador.backend.domain.entity.ProductSize;
import com.projeto.integrador.backend.dto.PageResponse;
import com.projeto.integrador.backend.dto.product.ProductRequest;
import com.projeto.integrador.backend.dto.product.ProductResponse;
import com.projeto.integrador.backend.dto.product.ProductSizeResponse;
import com.projeto.integrador.backend.exception.ResourceNotFoundException;
import com.projeto.integrador.backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public PageResponse<ProductResponse> getActiveProducts(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findByActiveTrue(pageable)
                .map(this::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public PageResponse<ProductResponse> searchActiveProducts(String query, Pageable pageable) {
        Page<ProductResponse> page = productRepository.searchActive(query, pageable)
                .map(this::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    /** Admin: retorna todos os produtos (ativos e inativos) com paginação */
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findAll(pageable)
                .map(this::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional
    public ProductResponse updateProductImage(UUID id, String imageUrl) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        product.setImageUrl(imageUrl);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        updateProductFields(product, request);
        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        product.getSizes().clear();
        updateProductFields(product, request);
        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public void deactivateProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    private void updateProductFields(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setCategory(request.category());
        product.getSizes().clear();
        request.sizes().forEach(sizeReq -> {
            ProductSize ps = new ProductSize(product, sizeReq.size(), sizeReq.stockQuantity());
            product.getSizes().add(ps);
        });
    }

    private ProductResponse toResponse(Product p) {
        List<ProductSizeResponse> sizes = p.getSizes().stream()
                .map(ps -> new ProductSizeResponse(ps.getId(), ps.getSize().name(), ps.getStockQuantity()))
                .toList();
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getImageUrl(), p.getCategory(), p.isActive(), sizes);
    }

    public ProductResponse toResponse1(Product product) {
        List<ProductSizeResponse> sizes = product.getSizes().stream()
                .map(ps -> new ProductSizeResponse(ps.getId(), ps.getSize().name(), ps.getStockQuantity()))
                .toList();
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                product.getImageUrl(), product.getCategory(), product.isActive(), sizes);
    }
}
