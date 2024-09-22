package org.concurrencyTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.concurrencyTest.dto.AllocationMessage;
import org.concurrencyTest.dto.PMResponseMessage;
import org.concurrencyTest.entity.ExpectedTransaction;
import org.concurrencyTest.repository.ExpectedTransactionsRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.concurrencyTest.entity.Status.PENDING_EXECUTION;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcurrencySuiteSubmitToOMS {
    /**
     * read all transactions in expected_tx for which pending = true
     * send to OMS Kafka
     * wait for response
     * send update to position service to insert a new tx with pending_execution
     * update the expected_tx pending = false
     * assert positions vs. expected_tx on pending_execution and signedOff
     */

    @Autowired
    private ExpectedTransactionsRepository expectedTransactionsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    //Ideally source these from OMS
    public static final String OMS_TOPIC = "orders_topic";
    public static final String PM_TOPIC = "pm-responses";

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencySuiteSubmitToOMS.class);
    private LinkedBlockingQueue<String> pmResponseQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = PM_TOPIC, groupId = "oms-group")
    public void listenToPMTopic(String message) {
        logger.info("received a message on the PM topic {}", message);
        pmResponseQueue.offer(message);
    }
    @Test
    public void testSendToOms() throws Exception {
       // while (true) { // TODO - switch to 1s interval checks ?
            for (ExpectedTransaction expectedTransaction : expectedTransactionsRepository.findByPending(true)) {
                //post a TX as a Kafka message to the OMS
                AllocationMessage message = new AllocationMessage(expectedTransaction.getOrderId(),expectedTransaction.getAccount().getAccountName(),
                        expectedTransaction.getProduct().getProductName(),"USD",expectedTransaction.getDirection(),expectedTransaction.getQuantity(),0);
                String jsonString = objectMapper.writeValueAsString(message);
                kafkaTemplate.send(new ProducerRecord<>(OMS_TOPIC, jsonString));
                //assert on  response
                //Assert on PM response
                String pmResponseMessage = pmResponseQueue.poll(5, TimeUnit.SECONDS);  // Wait for the PM response
                assertThat(pmResponseMessage).isNotNull();

                PMResponseMessage pmResponse = objectMapper.readValue(pmResponseMessage, PMResponseMessage.class);
                assertThat(pmResponse.getSourceOrderId()).isEqualTo(expectedTransaction.getOrderId());

                assertThat(pmResponse.getStatus()).isEqualTo(PENDING_EXECUTION.name());

                //wait for a while till the positionAggregation completes
                //compute positions aggregated from expected_tx and compare against positions returned
                //if OK, update expected_tx pending to false


            }
        Thread.sleep(2000); // pause for 2 s
        //}
    }
}
