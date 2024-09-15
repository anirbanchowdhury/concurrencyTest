package org.concurrencyTest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Product")
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;  // Changed to Long and made auto-increment

    @Column(nullable = false)
    private String productName;

    // getters and setters


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}