package com.banking.gateway.controller;

import com.banking.gateway.model.Transaction;
import com.banking.gateway.service.TransactionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TransactionController needs to:
 *  1. Receive POST /transactions -> deserialize JSON body into Transaction -> call service.processTransaction()
 *  2. Receive GET /transactions/{id} -> call service.getById() -> returns 404 if null, and 200 if found.
 *  3. Receive GET /transactions -> call service.getAll() -> returns all records (no pagination - intencional flaw)
 *  - No input validation, no erro handling, no pagination -> all intentional legacy problems.
 *  - Controller never touches the repository or PartnerClient directly, only the service.
 */

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = Logger.getLogger(TransactionController.class);

    @Autowired
    private static TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Void> process(@RequestBody Transaction transaction) {
        // Intentional legacy flaw - no input validation.
        // The client can't track the transaction it just submitted without querying separately.
        // A request with null partnerCode or negative amount goes staright to the service.
        logger.info("Received transaction for partner: " + transaction.getPartnerCode());

        transactionService.processTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);

        if (transaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return new ResponseEntity<>(transactionService.getAll(), HttpStatus.OK);

    }

}
