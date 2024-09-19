package org.concurrencyTest;

public class ConcurrencySuiteSubmitToOMS {
    /**
     * read all transactions in expected_tx for which pending = true
     * send to OMS Kafka
     * wait for response
     * send update to position service to insert a new tx with pending_execution
     * update the expected_tx pending = false
     * assert positions vs. expected_tx on pending_execution and signedOff
     */
}
