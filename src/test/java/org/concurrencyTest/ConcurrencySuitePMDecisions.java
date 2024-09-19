package org.concurrencyTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.RandomStringUtils;
import org.concurrencyTest.dto.ExpectedPosition;
import org.concurrencyTest.entity.*;
import org.concurrencyTest.repository.AccountRepository;
import org.concurrencyTest.repository.ExpectedTransactionsRepository;
import org.concurrencyTest.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ConcurrencySuitePMDecisions {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExpectedTransactionsRepository expectedTransactionRepository;

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencySuitePMDecisions.class);

    private  final String PM_SYSTEM = "CONC-TEST-PM1-";
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    public ConcurrencySuitePMDecisions(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Test
    public void testPMSubmissionSuite() throws Exception{
        final int NO_OF_PM_SUBMISSIONS = 1;
        final int SECONDS_TO_SLEEP = 5;
        for (int i=0;i <NO_OF_PM_SUBMISSIONS;i++){
            testPMSubmissionRandom();
            TimeUnit.SECONDS.sleep(SECONDS_TO_SLEEP);
            logger.info("Submission # {}", i);
        }
    }

    public void testPMSubmissionRandom() throws Exception {
        //  Pick random Account and Product from the database
        List<ExpectedTransaction> expectedTransactions = getRandomExpectedTransactions();

        List<String> orderIds =  expectedTransactions.stream()
                .map(ExpectedTransaction::getOrderId) // Extract the orderId
                .collect(Collectors.toList());
        logger.info("expected orderIds posted = {}",orderIds);
        Thread.sleep(1000); //TODO : change to await ?
        assertEquals(expectedTransactions.size(), expectedTransactionRepository.findByOrderIdIn(orderIds).size());

        Thread.sleep(5000);//wait for 5ms
        // TODO:  Use Awaitility to wait up to 5 seconds before checking the /api/positions response

        //Now scan across all expected_transactions to compute positions
        String positionsApiUrl = "http://localhost:8085/api/positions";

        // Deserialize the JSON array into a List of ExpectedPosition objects
        String jsonResponse = restTemplate.getForObject(positionsApiUrl, String.class);
        ObjectMapper mapper = getObjectMapper();
        List<ExpectedPosition> expectedPositions = mapper.readValue(jsonResponse, new TypeReference<>() {
        });

        logger.info("positions returned, count = {},  {}",expectedPositions.size(), expectedPositions);
        assertNotNull(expectedPositions, "Expected positions ");
        List<ExpectedTransaction> expectedTransactionsFromDB = expectedTransactionRepository.findByPending(true); //Only finding the PendingExecution = true, so just the signed off tx.
        compareExpectedTransactionsToPositionsReturned(expectedTransactionsFromDB,expectedPositions);
    }


    private void compareExpectedTransactionsToPositionsReturned(List<ExpectedTransaction> expectedTransactions, List<ExpectedPosition> expectedPositions) {
        // Group ExpectedTransaction by trade_dt, account_id, product_id
        Map<String, Integer> groupedTransactions = expectedTransactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTradeDt().toString() + "-" +
                                transaction.getAccount().getAccountName() + "-" +
                                transaction.getProduct().getProductName(),
                        Collectors.summingInt(transaction ->
                                transaction.getQuantity() * ("BUY".equals(transaction.getDirection()) ? 1 : -1)
                        )
                ));
        logger.info("grouped transactions = {}", groupedTransactions);

        // Compare grouped results with ExpectedPosition
        for (ExpectedPosition position : expectedPositions) {
            String key = position.getBd().toString() + "-" +
                    position.getAccount().getAccountName() + "-" +
                    position.getProduct().getProductName();
            logger.info(" grouped position key = {}, position = {}", key, position);
            if (groupedTransactions.containsKey(key)) {
                int expectedSignedOffQuantity = groupedTransactions.get(key);

                // Perform the comparison
                assertEquals(expectedSignedOffQuantity, position.getPmDecisionSignedOffQuantity(),
                        "Mismatch in pmDecisionSignedOffQuantity for account " + position.getAccount().getAccountName() +
                                " and product " + position.getProduct().getProductName());

                // Remove the matched transaction from the map
                groupedTransactions.remove(key);
            } else {
                assert false : "Couldn't find a transaction for the said position - impossible";
            }
        }

        // Check for any transactions that were not matched to a position
        if (!groupedTransactions.isEmpty()) {
            StringBuilder unmatchedTransactions = new StringBuilder("Unmatched transactions found which didnt have an equivalent position: ");
            groupedTransactions.forEach((key, quantity) -> {
                unmatchedTransactions.append(String.format("Key: %s, Quantity: %d; ", key, quantity));
            });
            assert false : unmatchedTransactions.toString();
        }
    }


    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule to handle LocalDateTime
        JavaTimeModule module = new JavaTimeModule();
        // Register module with custom formatter for LocalDateTime (if needed)
        mapper.registerModule(module);
        /*// Optionally disable failing on unknown properties (if there are many differences)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // Disable WRITE_DATES_AS_TIMESTAMPS if you want ISO 8601 formatting for dates
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
*/
        return mapper;
    }

    private List<ExpectedTransaction> getRandomExpectedTransactions() {
        List<ExpectedTransaction> expectedTransactions = new ArrayList<>();
        //TODO : fetch accounts and products from another API
        List<Account> accounts = accountRepository.findAll();
        List<Product> products = productRepository.findAll();

        Random random = new Random();
        int X = random.nextInt(3) + 1;  // X between 1 and 3
        int Y = random.nextInt(4) + 1;  // Y between 1 and 4

        ExecutorService executorService = Executors.newFixedThreadPool(X * Y);
        //  Generate random quantity and direction and create a new tx
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                String orderId = PM_SYSTEM + RandomStringUtils.randomAlphanumeric(7);
                int randomQuantity = random.nextInt(100) + 1;
                String randomDirection = random.nextBoolean() ? "BUY" : "SELL";
                int accountIndex = random.nextInt(accounts.size());
                int productIndex = random.nextInt(products.size());
                Account randomAccount = accounts.get(accountIndex);
                Product randomProduct = products.get(productIndex);

                //  Create ExpectedTransaction entity
                ExpectedTransaction expectedTransaction = new ExpectedTransaction();
                expectedTransaction.setOrderId(orderId);
                expectedTransaction.setAccount(randomAccount);
                expectedTransaction.setProduct(randomProduct);
                expectedTransaction.setTradeDt(new java.util.Date());
                expectedTransaction.setDirection(randomDirection);
                expectedTransaction.setQuantity(randomQuantity);
                expectedTransaction.setAggregationStatus(AggregationStatus.PENDING);
                expectedTransaction.setPending(true);
                expectedTransaction.setFromDt(LocalDateTime.now());
                expectedTransaction.setFilledQuantity(0);
                logger.info("Expected transaction object = {}", expectedTransaction);

                // Create the JSON from ExpectedTransaction getters

                String transactionJson = "{ " +
                        "\"orderId\":\"" + expectedTransaction.getOrderId() + "\", " +
                        "\"account\": { \"accountName\": \"" + expectedTransaction.getAccount().getAccountName() + "\" }, " +
                        "\"product\": { \"productName\": \"" + expectedTransaction.getProduct().getProductName() + "\" }, " +
                        "\"tradeDt\": \"" + formatter.format(expectedTransaction.getTradeDt()) + "\", " +
                        "\"direction\": \"" + expectedTransaction.getDirection() + "\", " +
                        "\"quantity\": " + expectedTransaction.getQuantity() + ", " +
                        "\"source\": \"" + "PLANNING" + "\", " +
                        "\"aggregationStatus\": \"" + expectedTransaction.getAggregationStatus() + "\", " +
                        "\"status\": \"" + (expectedTransaction.isPending()?Status.PM_DECISION.name():Status.PENDING_EXECUTION.name())+ "\", " +
                        "\"fromDt\": \"" + expectedTransaction.getFromDt().format(DateTimeFormatter.ISO_DATE_TIME) + "\" " +
                        "}";

                logger.info("transaction json posted {}", transactionJson);
                expectedTransactions.add(expectedTransaction);


                executorService.submit(() -> {
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        String transactionApiUrl = "http://localhost:8085/api/transactions";
                        // Create the HTTP entity with headers and body
                        HttpEntity<String> entity = new HttpEntity<>(transactionJson, headers);
                        // Perform the POST request
                        ResponseEntity<String> postResponse = restTemplate.exchange(transactionApiUrl, HttpMethod.POST, entity, String.class);

                        // Assert that the response status is OK (200)
                        assertEquals(HttpStatus.OK, postResponse.getStatusCode(), "The response status should be OK (200)");
                        //Save the random expectedTransaction when received OK from API
                        expectedTransactionRepository.save(expectedTransaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        logger.info("expected tranactions = {}",expectedTransactions);
        logger.info("Expected transactions size = {}",expectedTransactions.size());
        return expectedTransactions;
    }
}

