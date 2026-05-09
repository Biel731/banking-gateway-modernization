package com.banking.gateway.repository;

import com.banking.gateway.model.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TransactionRepository needs to:
 *  1. Received SessionFactory from Spring (already configured in applicationContext.xml)
 *  2. Save a new Transaction in the database
 *  3. Find a Transaction by ID
 *  4. List all Transactions (no pagination - intentional legacy flaw)
 *  5. No transaction management here - that's the service's repository later
 */

@Repository
public class TransactionRepository {
    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Transaction transaction) {
        currentSession().save(transaction);
    }

    public Transaction findById(Long id) {
        return (Transaction) currentSession().get(Transaction.class, id);
    }

    public List<Transaction> findAll() {
        return currentSession().
                createQuery("from Transaction").
                list();
    }
}
