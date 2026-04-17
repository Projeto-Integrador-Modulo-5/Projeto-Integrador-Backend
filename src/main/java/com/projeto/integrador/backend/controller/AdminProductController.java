package com.projeto.integrador.backend.controller;

import com.projeto.integrador.backend.dto.product.ProductRequest;
import com.projeto.integrador.backend.dto.product.ProductResponse;
import com.projeto.integrador.backend.service.FileUploadService;
import com.projeto.integrador.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Admin – Produtos", description = "Gerenciamento de produtos (requer ADMIN)")
@RestController
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final FileUploadService fileUploadService;

    public AdminProductController(ProductService productService, FileUploadService fileUploadService) {
        this.productService    = productService;
        this.fileUploadService = fileUploadService;
    }

    @Operation(summary = "Criar produto")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(201).body(productService.createProduct(request));
    }

    @Operation(summary = "Atualizar produto")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id,
                                                         @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @Operation(summary = "Fazer upload da imagem do produto",
               description = "Envia multipart/form-data com campo 'file' (jpeg, png, webp, gif – máx 5 MB)")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> uploadImage(@PathVariable UUID id,
                                                       @RequestParam("file") MultipartFile file) {
        String imageUrl = fileUploadService.store(file);
        return ResponseEntity.ok(productService.updateProductImage(id, imageUrl));
    }

    @Operation(summary = "Desativar produto (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable UUID id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }
}
