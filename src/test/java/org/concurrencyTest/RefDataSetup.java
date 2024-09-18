package org.concurrencyTest;

import org.concurrencyTest.entity.Account;
import org.concurrencyTest.entity.Product;
import org.concurrencyTest.repository.AccountRepository;
import org.concurrencyTest.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RefDataSetup {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void setupRefData() throws Exception {
        //ideally this is called in the setup but doing a dirty POC here which can be called on demand
        accountRepository.deleteAll();
        for(int i = 1; i <=15;i++){
            accountRepository.save(new Account("AC"+i));
        }
        productRepository.deleteAll();
        for(int i = 1; i <=20;i++){
            productRepository.save(new Product("P"+i));
        }

    }
}