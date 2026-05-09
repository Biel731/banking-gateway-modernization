package com.banking.gateway.client;

import com.banking.gateway.model.Transaction;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PartnerClient {
    private static final Logger logger = Logger.getLogger(PartnerClient.class);

    // Intentional legacy flaw: RestTemplate created with no timeout configuration.
    // If the external partner is slow or unresponsive, this thread blocks indefinitely.
    // Under load, the exhausts the entire thread pool and brings down the application.
    private final RestTemplate restTemplate = new RestTemplate();

    public String sendTransaction(Transaction transaction, String partnerUrl) {
        logger.info("Sending transaction " + transaction.getId() + "to partner: " + partnerUrl);

        // Intentionaly lgacy flaw: no retry, no circuit breaker, no fallback
        // A single slow partner affects all other transactions in the system
        String response = restTemplate.postForObject(partnerUrl, transaction, String.class);

        logger.info("Partner response for transaction " + transaction.getId() + ": " + response);
        return response;
    }
}
