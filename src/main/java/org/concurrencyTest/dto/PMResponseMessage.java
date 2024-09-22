package org.concurrencyTest.dto;

public class PMResponseMessage {
    private String sourceOrderId;
    private String status; // PENDING_EXECUTION / EXECUTION  TODO - convert to one enum

    private int executedQuantity; // total executedQty so far

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(int executedQuantity) {
        this.executedQuantity = executedQuantity;
    }
}
