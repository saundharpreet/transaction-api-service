package com.harpreetsaund.transactionapiservice.repository;

import com.harpreetsaund.transactionapiservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    Optional<Transaction> findByEventId(String eventId);

    List<Transaction> findByAccountNumber(String accountNumber);

    List<Transaction> findByAccountNumberAndTransactionType(String accountNumber, String transactionType);

    boolean existsByTransactionId(String transactionId);

    boolean existsByEventId(String eventId);
}
