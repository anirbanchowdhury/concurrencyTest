package org.concurrencyTest.dto;

public class AllocationMessage {
    private String sourceOrderId;
    private String accountName;
    private String productName;
    private String ccy;
    private String direction;
    private int originalQuantity;
    private int allocatedQuantity;

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public int getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(int originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    public int getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(int allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public AllocationMessage() {
    }

    public AllocationMessage(String sourceOrderId, String accountName, String productName, String ccy, String direction, int originalQuantity, int allocatedQuantity) {
        this.sourceOrderId = sourceOrderId;
        this.accountName = accountName;
        this.productName = productName;
        this.ccy = ccy;
        this.direction = direction;
        this.originalQuantity = originalQuantity;
        this.allocatedQuantity = allocatedQuantity;
    }

    @Override
    public String toString() {
        return "AllocationMessage{" +
                "sourceOrderId='" + sourceOrderId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", productName='" + productName + '\'' +
                ", ccy='" + ccy + '\'' +
                ", direction='" + direction + '\'' +
                ", originalQuantity=" + originalQuantity +
                ", allocatedQuantity=" + allocatedQuantity +
                '}';
    }
}