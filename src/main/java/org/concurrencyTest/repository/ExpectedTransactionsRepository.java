package org.concurrencyTest.repository;


import org.concurrencyTest.entity.AggregationStatus;
import org.concurrencyTest.entity.ExpectedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpectedTransactionsRepository extends JpaRepository<ExpectedTransaction, Long> {

    Optional<ExpectedTransaction> findByAggregationStatus(AggregationStatus aggregationStatus);

    List<ExpectedTransaction> findTop10ByAggregationStatusNotOrderByTradeDtAsc(AggregationStatus aggregationStatus);

    List<ExpectedTransaction> findByOrderIdIn(List<String> orderIds);
}

