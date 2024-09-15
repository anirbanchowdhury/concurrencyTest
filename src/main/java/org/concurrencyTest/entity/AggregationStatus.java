package org.concurrencyTest.entity;

public enum AggregationStatus {
    PENDING(0),
    COMPLETED(1);

    private int value;

    AggregationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AggregationStatus fromValue(int value) {
        for (AggregationStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AggregationStatus value: " + value);
    }
}
