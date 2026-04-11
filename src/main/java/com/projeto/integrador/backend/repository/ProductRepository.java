package com.projeto.integrador.backend.repository;

import com.projeto.integrador.backend.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByActiveTrue();
}
