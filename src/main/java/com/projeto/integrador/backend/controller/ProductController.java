package com.projeto.integrador.backend.controller;

import com.projeto.integrador.backend.dto.PageResponse;
import com.projeto.integrador.backend.dto.product.ProductResponse;
import com.projeto.integrador.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Produtos", description = "Catálogo público de produtos")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Listar produtos ativos com paginação",
               description = "Parâmetros: page (0-based), size (default 12), sort (ex: name,asc)")
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> listProducts(
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(productService.getActiveProducts(pageable));
    }

    @Operation(summary = "Buscar produto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
