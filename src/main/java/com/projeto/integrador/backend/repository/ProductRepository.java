package com.projeto.integrador.backend.repository;

import com.projeto.integrador.backend.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByActiveTrue();
    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
          AND (
            LOWER(p.name)        LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.category)    LIKE LOWER(CONCAT('%', :q, '%'))
          )
        """)
    Page<Product> searchActive(@Param("q") String q, Pageable pageable);
}
