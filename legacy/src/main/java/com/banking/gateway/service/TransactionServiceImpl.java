package com.banking.gateway.service;

import com.banking.gateway.client.PartnerClient;
import com.banking.gateway.model.Transaction;
import com.banking.gateway.repository.TransactionRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TransactionServiceImpl needs to:
 *  1. Receive a Transaction from the controller (not yet built)
 *  2. Apply routing logic based on patner code (if/else intentional flaw)
 *  3. Set the transactional status based on routing result
 *  4. Delegate persistence to TransactionRepository
 *  5. Own the @Transactional boudary - the repository does NOT manage transactions
 *  No idempotency check - intentional: the same request processed twice - two records.
 */

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = Logger.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PartnerClient partnerClient;

    @Override
    public void processTransaction(Transaction transaction) {

        String partnerUrl;
        // Intentional legacy flaw: No idempontecy check.
        // The same transaction can be saved multiple times if the client retries.

        if("APPROVED".equals(transaction.getPartnerCode())) {
            transaction.setPartnerCode("APPROVED");
            logger.info("Transaction approved for the partner: " + transaction.getPartnerCode());
        } else if ("BLOCKED".equals(transaction.getPartnerCode())){
            transaction.setStatus("BLOCKED");
            logger.info("Transaction blocked for patner: " + transaction.getPartnerCode());
        } else {
            // Intentional legacy flaw: routing logic is a hard-coded if/else chain.
            // Adding new partners requiries modify this method directly.
            transaction.setStatus("UNKNOWN_PARTNER");
            logger.error("Unknown partner code: " + transaction.getPartnerCode());
        }

        // Intentional legacy flaw: routing logic is a hard-coded if/else
        // Adding a new parter requires modifying this method and redeploying.
        if ("BANK_A".equals(transaction.getPartnerCode())) {
            partnerUrl = "http://bank_a.internal/api/transactions";
        } else if ("CARD_NETWORK".equals(transaction.getPartnerCode())) {
            partnerUrl = "http://card-network.internal/api/transactions";
        } else {
            logger.error("Unknown partner code: " + transaction.getPartnerCode());
            transaction.setStatus("REJECTED");
            transactionRepository.save(transaction);
            return;
        }

        // Intentional legacy flaw: no idempotency check before calling the partner
        // -> A retry from the client sends a duplicate transaction to the partner

        String partnerResponse = partnerClient.sendTransaction(transaction, partnerUrl);

        transaction.setStatus(partnerResponse);
        transactionRepository.save(transaction);
    }

    @Override
    public Transaction getById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAll() {
        // Intentional legacy flaw: returns all records with no pagination
        // In production, this could return millions of rows and crash the JVM.
        return transactionRepository.findAll();
    }
}
