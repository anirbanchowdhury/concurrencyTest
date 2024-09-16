package org.concurrencyTest.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Expected_Transaction")
public class ExpectedTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)  // Add relationship with Account
    private Account account;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)  // Add relationship with Product
    private Product product;
    private Date tradeDt;
    private String direction;
    private Integer quantity;

    @Enumerated(EnumType.ORDINAL)
    private AggregationStatus aggregationStatus;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    private Integer filledQuantity;

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

    public Date getTradeDt() {
        return tradeDt;
    }

    public void setTradeDt(Date tradeDt) {
        this.tradeDt = tradeDt;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public AggregationStatus getAggregationStatus() {
        return aggregationStatus;
    }

    public void setAggregationStatus(AggregationStatus aggregationStatus) {
        this.aggregationStatus = aggregationStatus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Integer filledQuantity) {
        this.filledQuantity = filledQuantity;
    }


    @Override
    public String toString() {
        return "ExpectedTransaction{" +
                "id=" + id +
                ", account=" + account +
                ", product=" + product +
                ", tradeDt=" + tradeDt +
                ", direction='" + direction + '\'' +
                ", quantity=" + quantity +
                ", aggregationStatus=" + aggregationStatus +
                ", status=" + status +
                ", filledQuantity=" + filledQuantity +
                ", fromDt=" + fromDt +
                ", toDt=" + toDt +
                '}';
    }
}
