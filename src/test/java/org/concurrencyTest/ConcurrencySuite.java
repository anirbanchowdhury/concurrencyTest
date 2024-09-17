package org.concurrencyTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.awaitility.Awaitility;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mysql.cj.conf.PropertyKey.logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ConcurrencySuite {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExpectedTransactionsRepository expectedTransactionRepository;

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencySuite.class);

    @Autowired
    public ConcurrencySuite(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Test
    public void testPostTransactionAndValidatePositions() throws Exception {
        //  Pick random Account and Product from the database
        List<ExpectedTransaction> expectedTransactions = getRandomExpectedTransactions();
        assertEquals(expectedTransactions.size(),expectedTransactionRepository.findAll().size());

        Thread.sleep(5000);//wait for 5ms
        //  Use Awaitility to wait up to 5 seconds before checking the /api/positions response

        String positionsApiUrl = "http://localhost:8085/api/positions";

        // Deserialize the JSON array into a List of ExpectedPosition objects
        String jsonResponse = restTemplate.getForObject(positionsApiUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        List<ExpectedPosition> expectedPositions = mapper.readValue(jsonResponse, new TypeReference<List<ExpectedPosition>>() {
        });
        logger.info("positions returned {}", expectedPositions);
        assertNotNull(expectedPositions, "Expected positions ");


    }


    private List<ExpectedTransaction> getRandomExpectedTransactions(){

        List<ExpectedTransaction> expectedTransactions = new ArrayList<>();

        //TODO : fetch accounts and products from another API
        List<Account> accounts = accountRepository.findAll();
        List<Product> products = productRepository.findAll();

        Random random = new Random();
        int X = random.nextInt(3) + 1;  // X between 1 and 3
        int Y = random.nextInt(4) + 1;  // Y between 1 and 4

        ExecutorService executorService = Executors.newFixedThreadPool(X * Y);

        //  Generate random quantity and direction
        int randomQuantity = random.nextInt(100) + 1;
        String randomDirection = random.nextBoolean() ? "BUY" : "SELL";

        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                int accountIndex = random.nextInt(accounts.size());
                int productIndex = random.nextInt(products.size());

                Account randomAccount = accounts.get(accountIndex);
                Product randomProduct = products.get(productIndex);
                //  Create ExpectedTransaction entity
                ExpectedTransaction expectedTransaction = new ExpectedTransaction();
                expectedTransaction.setAccount(randomAccount);
                expectedTransaction.setProduct(randomProduct);
                expectedTransaction.setTradeDt(new java.util.Date());
                expectedTransaction.setDirection(randomDirection);
                expectedTransaction.setQuantity(randomQuantity);
                expectedTransaction.setAggregationStatus(AggregationStatus.PENDING);
                expectedTransaction.setStatus(Status.PM_DECISION);
                expectedTransaction.setFromDt(LocalDateTime.now());
                //TODO - set to default 0
                expectedTransaction.setFilledQuantity(0);
                logger.info("Expected transaction object = {}", expectedTransaction);


                // Create the JSON from ExpectedTransaction getters
                String transactionJson = "{ " +
                        "\"account\": { \"accountName\": \"" + expectedTransaction.getAccount().getAccountName() + "\" }, " +
                        "\"product\": { \"productName\": \"" + expectedTransaction.getProduct().getProductName() + "\" }, " +
                        "\"tradeDt\": \"" + "2024-09-16" + "\", " + //TODO change to date
                        "\"direction\": \"" + expectedTransaction.getDirection() + "\", " +
                        "\"quantity\": " + expectedTransaction.getQuantity() + ", " +
                        "\"source\": \"" + "PLANNING" + "\", " +
                        "\"aggregationStatus\": \"" + expectedTransaction.getAggregationStatus() + "\", " +
                        "\"status\": \"" + expectedTransaction.getStatus() + "\", " +
                        "\"fromDt\": \"" + expectedTransaction.getFromDt().format(DateTimeFormatter.ISO_DATE_TIME) + "\" " +
                        "}";

                logger.info("transaction json posted {}", transactionJson);
               /* expectedTransactionJSONs.add(transactionJson);
                expectedTransactions.add(expectedTransaction);*/


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


        return expectedTransactions;
    }
}

