package com.harpreetsaund.transactionapiservice.service;

import com.harpreetsaund.transactionapiclient.dto.TransactionRequest;
import com.harpreetsaund.transactionapiclient.dto.TransactionResponse;
import com.harpreetsaund.transactionapiservice.mapper.TransactionMapper;
import com.harpreetsaund.transactionapiservice.model.Transaction;
import com.harpreetsaund.transactionapiservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionApiService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionApiService.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public TransactionApiService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionResponse.Version1 createTransaction(TransactionRequest.Version1 request) {
        logger.info("Creating transaction with eventId: {}", request.getEventId());

        if (transactionRepository.existsByEventId(request.getEventId())) {
            throw new IllegalArgumentException("Transaction with eventId already exists: " + request.getEventId());
        }

        Transaction transaction = transactionMapper.toTransaction(request);
        transaction.setId(null); // Ensure ID is null for new entity
        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Transaction created with id: {}", savedTransaction.getId());
        return transactionMapper.toTransactionResponseVersion1(savedTransaction);
    }

    @Transactional(readOnly = true)
    public Optional<TransactionResponse.Version1> getTransactionById(Long id) {
        logger.info("Fetching transaction by id: {}", id);
        return transactionRepository.findById(id).map(transactionMapper::toTransactionResponseVersion1);
    }

    @Transactional(readOnly = true)
    public Optional<TransactionResponse.Version1> getTransactionByTransactionId(String transactionId) {
        logger.info("Fetching transaction by transactionId: {}", transactionId);
        return transactionRepository.findByTransactionId(transactionId)
                .map(transactionMapper::toTransactionResponseVersion1);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse.Version1> getTransactionsByAccountNumber(String accountNumber) {
        logger.info("Fetching transactions for account: {}", accountNumber);
        return transactionRepository.findByAccountNumber(accountNumber).stream()
                .map(transactionMapper::toTransactionResponseVersion1).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse.Version1> getAllTransactions() {
        logger.info("Fetching all transactions");
        return transactionRepository.findAll().stream().map(transactionMapper::toTransactionResponseVersion1)
                .collect(Collectors.toList());
    }

    public TransactionResponse.Version1 updateTransaction(String transactionId, TransactionRequest.Version1 request) {
        logger.info("Updating transaction with transactionId: {}", transactionId);

        Transaction existingTransaction = transactionRepository.findByTransactionId(transactionId).orElseThrow(
                () -> new IllegalArgumentException("Transaction not found with transactionId: " + transactionId));

        // Update fields from request
        existingTransaction.setEventId(request.getEventId());
        existingTransaction.setAccountNumber(request.getAccountNumber());
        existingTransaction.setTransactionType(request.getTransactionType());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setCurrency(request.getCurrency());
        existingTransaction.setMerchantName(request.getMerchantName());
        existingTransaction.setChannel(request.getChannel());
        existingTransaction.setTransactionTimestamp(request.getTransactionTimestamp());
        existingTransaction.setSourceSystem(request.getSourceSystem());
        existingTransaction.setTopicName(request.getTopicName());

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        logger.info("Transaction updated with id: {}", updatedTransaction.getId());
        return transactionMapper.toTransactionResponseVersion1(updatedTransaction);
    }

    public void deleteTransaction(String transactionId) {
        logger.info("Deleting transaction with transactionId: {}", transactionId);

        Transaction transaction = transactionRepository.findByTransactionId(transactionId).orElseThrow(
                () -> new IllegalArgumentException("Transaction not found with transactionId: " + transactionId));

        transactionRepository.delete(transaction);
        logger.info("Transaction deleted with id: {}", transaction.getId());
    }
}
