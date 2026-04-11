package com.projeto.integrador.backend.domain.entity;

import com.projeto.integrador.backend.domain.enums.Size;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "product_sizes")
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    public ProductSize() {}

    public ProductSize(Product product, Size size, int stockQuantity) {
        this.product = product;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
