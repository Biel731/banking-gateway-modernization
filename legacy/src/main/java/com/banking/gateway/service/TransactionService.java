package com.banking.gateway.service;

import com.banking.gateway.model.Transaction;
import java.util.List;

public interface TransactionService {
    void processTransaction(Transaction transaction);
    Transaction getById(Long id);
    List<Transaction> getAll();
}
