package org.concurrencyTest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.concurrencyTest.entity.Account;
import org.concurrencyTest.entity.Product;


import java.time.LocalDateTime;

public class ExpectedPosition {

    private Long id;
    private Account account;
    private Product product;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String bd;



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

    private Integer pmDecisionSignedOffQuantity = 0;

    private Integer pendingExecutionQuantity = 0;
    private Integer executedQuantity = 0;

    public LocalDateTime getFromDt() {
        return fromDt;
    }

    public Integer getPmDecisionSignedOffQuantity() {
        return pmDecisionSignedOffQuantity;
    }

    public void setPmDecisionSignedOffQuantity(Integer pmDecisionSignedOffQuantity) {
        this.pmDecisionSignedOffQuantity = pmDecisionSignedOffQuantity;
    }

    public Integer getPendingExecutionQuantity() {
        return pendingExecutionQuantity;
    }

    public void setPendingExecutionQuantity(Integer pendingExecutionQuantity) {
        this.pendingExecutionQuantity = pendingExecutionQuantity;
    }

    public Integer getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(Integer executedQuantity) {
        this.executedQuantity = executedQuantity;
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

    @Override
    public String toString() {
        return "ExpectedPosition{" +
                "id=" + id +
                ", account=" + account +
                ", product=" + product +
                ", bd='" + bd + '\'' +
                ", fromDt=" + fromDt +
                ", toDt=" + toDt +
                ", pmDecisionSignedOffQuantity=" + pmDecisionSignedOffQuantity +
                ", pendingExecutionQuantity=" + pendingExecutionQuantity +
                ", executedQuantity=" + executedQuantity +
                '}';
    }
}
