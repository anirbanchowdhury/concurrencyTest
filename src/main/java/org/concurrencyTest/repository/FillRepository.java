package org.concurrencyTest.repository;

import org.concurrencyTest.entity.ExpectedFill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FillRepository extends JpaRepository<ExpectedFill, Long> {
    List<ExpectedFill> findByAllocation(Allocation allocation);
}