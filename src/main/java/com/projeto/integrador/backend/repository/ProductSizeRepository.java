package com.projeto.integrador.backend.repository;

import com.projeto.integrador.backend.domain.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {
    List<ProductSize> findByProductId(UUID productId);
}
