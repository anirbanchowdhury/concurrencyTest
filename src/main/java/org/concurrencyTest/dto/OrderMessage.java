package org.concurrencyTest.dto;

import java.time.LocalDate;
import java.util.List;

public class OrderMessage {
    private String sourceId;
    private String productName;
    private String ccy;
    private String direction;
    private int quantity;
    private LocalDate fromDt;
    private LocalDate thruDt;
    private List<AllocationMessage> allocations;

    // getters and setters

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getFromDt() {
        return fromDt;
    }

    public void setFromDt(LocalDate fromDt) {
        this.fromDt = fromDt;
    }

    public LocalDate getThruDt() {
        return thruDt;
    }

    public void setThruDt(LocalDate thruDt) {
        this.thruDt = thruDt;
    }

    public List<AllocationMessage> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<AllocationMessage> allocations) {
        this.allocations = allocations;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "sourceId='" + sourceId + '\'' +
                ", productName='" + productName + '\'' +
                ", ccy='" + ccy + '\'' +
                ", direction='" + direction + '\'' +
                ", quantity=" + quantity +
                ", fromDt=" + fromDt +
                ", thruDt=" + thruDt +
                ", allocations=" + allocations +
                '}';
    }
}
