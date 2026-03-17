package com.harpreetsaund.transactionapiservice.mapper;

import com.harpreetsaund.transactionapiclient.dto.TransactionRequest;
import com.harpreetsaund.transactionapiclient.dto.TransactionResponse;
import com.harpreetsaund.transactionapiservice.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TransactionMapper Tests")
class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapper();
    }

    @Test
    @DisplayName("Should map TransactionRequest.Version1 to Transaction")
    void testToTransaction() {
        // Arrange
        TransactionRequest.Version1 request = new TransactionRequest.Version1();
        request.setEventId("evt-123");
        request.setTransactionId("txn-001");
        request.setAccountNumber("ACC-12345");
        request.setTransactionType("DEBIT");
        request.setAmount(BigDecimal.valueOf(100.50));
        request.setCurrency("USD");
        request.setMerchantName("Test Merchant");
        request.setChannel("ATM");
        request.setTransactionTimestamp(Instant.parse("2023-10-01T12:00:00Z"));
        request.setSourceSystem("CORE");
        request.setTopicName("transaction-events");

        // Act
        Transaction transaction = transactionMapper.toTransaction(request);

        // Assert
        assertNull(transaction.getId());
        assertEquals("evt-123", transaction.getEventId());
        assertEquals("txn-001", transaction.getTransactionId());
        assertEquals("ACC-12345", transaction.getAccountNumber());
        assertEquals("DEBIT", transaction.getTransactionType());
        assertEquals(BigDecimal.valueOf(100.50), transaction.getAmount());
        assertEquals("USD", transaction.getCurrency());
        assertEquals("Test Merchant", transaction.getMerchantName());
        assertEquals("ATM", transaction.getChannel());
        assertEquals(Instant.parse("2023-10-01T12:00:00Z"), transaction.getTransactionTimestamp());
        assertEquals("CORE", transaction.getSourceSystem());
        assertEquals("transaction-events", transaction.getTopicName());
    }

    @Test
    @DisplayName("Should map Transaction to TransactionResponse.Version1")
    void testToTransactionResponseVersion1() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setEventId("evt-123");
        transaction.setTransactionId("txn-001");
        transaction.setAccountNumber("ACC-12345");
        transaction.setTransactionType("DEBIT");
        transaction.setAmount(BigDecimal.valueOf(100.50));
        transaction.setCurrency("USD");
        transaction.setMerchantName("Test Merchant");
        transaction.setChannel("ATM");
        transaction.setTransactionTimestamp(Instant.parse("2023-10-01T12:00:00Z"));
        transaction.setSourceSystem("CORE");
        transaction.setTopicName("transaction-events");
        transaction.setCreatedAt(Instant.parse("2023-10-01T12:00:00Z"));

        // Act
        TransactionResponse.Version1 response = transactionMapper.toTransactionResponseVersion1(transaction);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("evt-123", response.getEventId());
        assertEquals("txn-001", response.getTransactionId());
        assertEquals("ACC-12345", response.getAccountNumber());
        assertEquals("DEBIT", response.getTransactionType());
        assertEquals(BigDecimal.valueOf(100.50), response.getAmount());
        assertEquals("USD", response.getCurrency());
        assertEquals("Test Merchant", response.getMerchantName());
        assertEquals("ATM", response.getChannel());
        assertEquals(Instant.parse("2023-10-01T12:00:00Z"), response.getTransactionTimestamp());
        assertEquals("CORE", response.getSourceSystem());
        assertEquals("transaction-events", response.getTopicName());
        assertEquals(Instant.parse("2023-10-01T12:00:00Z"), response.getCreatedAt());
    }
}
