package org.concurrencyTest.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Expected_Fill")
public class ExpectedFill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fillId;

    /*TODO - add Order / Allocation ID to link along */
    @Column(nullable = false)
    private int fillQuantity;

    @Column(nullable = false)
    private LocalDate fromDt;

    @Column(nullable = false)
    private LocalDate thruDt;

    // getters and setters

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public int getFillQuantity() {
        return fillQuantity;
    }

    public void setFillQuantity(int fillQuantity) {
        this.fillQuantity = fillQuantity;
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
}
