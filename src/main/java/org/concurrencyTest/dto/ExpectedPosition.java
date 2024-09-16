package org.concurrencyTest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.concurrencyTest.entity.Account;
import org.concurrencyTest.entity.Product;

import java.time.LocalDateTime;

public class ExpectedPosition {

    private Long id;
    private Account account;
    private Product product;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String bd;
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime fromDt;
    private LocalDateTime toDt;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getBd() {
        return bd;
    }

    public void setBd(String bd) {
        this.bd = bd;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getFromDt() {
        return fromDt;
    }

    public void setFromDt(LocalDateTime fromDt) {
        this.fromDt = fromDt;
    }

    public LocalDateTime getToDt() {
        return toDt;
    }

    public void setToDt(LocalDateTime toDt) {
        this.toDt = toDt;
    }
}
