package com.harpreetsaund.transactionapiservice.service;

import com.harpreetsaund.transactionapiclient.dto.TransactionRequest;
import com.harpreetsaund.transactionapiclient.dto.TransactionResponse;
import com.harpreetsaund.transactionapiservice.mapper.TransactionMapper;
import com.harpreetsaund.transactionapiservice.model.Transaction;
import com.harpreetsaund.transactionapiservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionApiService Tests")
class TransactionApiServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionApiService transactionApiService;

    private TransactionRequest.Version1 request;
    private Transaction transaction;
    private TransactionResponse.Version1 response;

    @BeforeEach
    void setUp() {
        request = new TransactionRequest.Version1();
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

        transaction = new Transaction();
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

        response = new TransactionResponse.Version1();
        response.setId(1L);
        response.setEventId("evt-123");
        response.setTransactionId("txn-001");
        response.setAccountNumber("ACC-12345");
        response.setTransactionType("DEBIT");
        response.setAmount(BigDecimal.valueOf(100.50));
        response.setCurrency("USD");
        response.setMerchantName("Test Merchant");
        response.setChannel("ATM");
        response.setTransactionTimestamp(Instant.parse("2023-10-01T12:00:00Z"));
        response.setSourceSystem("CORE");
        response.setTopicName("transaction-events");
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void testCreateTransaction() {
        // Arrange
        when(transactionRepository.existsByEventId("evt-123")).thenReturn(false);
        when(transactionMapper.toTransaction(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toTransactionResponseVersion1(transaction)).thenReturn(response);

        // Act
        TransactionResponse.Version1 result = transactionApiService.createTransaction(request);

        // Assert
        assertNotNull(result);
        assertEquals(response, result);
        verify(transactionRepository).existsByEventId("evt-123");
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionMapper).toTransaction(request);
        verify(transactionMapper).toTransactionResponseVersion1(transaction);
    }

    @Test
    @DisplayName("Should throw exception when eventId already exists")
    void testCreateTransaction_EventIdExists() {
        // Arrange
        when(transactionRepository.existsByEventId("evt-123")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionApiService.createTransaction(request));
        assertEquals("Transaction with eventId already exists: evt-123", exception.getMessage());
        verify(transactionRepository).existsByEventId("evt-123");
        verifyNoMoreInteractions(transactionRepository, transactionMapper);
    }

    @Test
    @DisplayName("Should get transaction by id")
    void testGetTransactionById() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionResponseVersion1(transaction)).thenReturn(response);

        // Act
        Optional<TransactionResponse.Version1> result = transactionApiService.getTransactionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(transactionRepository).findById(1L);
        verify(transactionMapper).toTransactionResponseVersion1(transaction);
    }

    @Test
    @DisplayName("Should return empty when transaction not found by id")
    void testGetTransactionById_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<TransactionResponse.Version1> result = transactionApiService.getTransactionById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(transactionRepository).findById(1L);
        verifyNoInteractions(transactionMapper);
    }

    @Test
    @DisplayName("Should get transaction by transactionId")
    void testGetTransactionByTransactionId() {
        // Arrange
        when(transactionRepository.findByTransactionId("txn-001")).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionResponseVersion1(transaction)).thenReturn(response);

        // Act
        Optional<TransactionResponse.Version1> result = transactionApiService.getTransactionByTransactionId("txn-001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(transactionRepository).findByTransactionId("txn-001");
        verify(transactionMapper).toTransactionResponseVersion1(transaction);
    }

    @Test
    @DisplayName("Should get transactions by account number")
    void testGetTransactionsByAccountNumber() {
        // Arrange
        when(transactionRepository.findByAccountNumber("ACC-12345")).thenReturn(List.of(transaction));
        when(transactionMapper.toTransactionResponseVersion1(transaction)).thenReturn(response);

        // Act
        List<TransactionResponse.Version1> result = transactionApiService.getTransactionsByAccountNumber("ACC-12345");

        // Assert
        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(transactionRepository).findByAccountNumber("ACC-12345");
        verify(transactionMapper).toTransactionResponseVersion1(transaction);
    }

    @Test
    @DisplayName("Should get all transactions")
    void testGetAllTransactions() {
        // Arrange
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        when(transactionMapper.toTransactionResponseVersion1(transaction)).thenReturn(response);

        // Act
        List<TransactionResponse.Version1> result = transactionApiService.getAllTransactions();

        // Assert
        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(transactionRepository).findAll();
        verify(transactionMapper).toTransactionResponseVersion1(transaction);
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void testUpdateTransaction() {
        // Arrange
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(1L);
        updatedTransaction.setEventId("evt-456");
        // ... set other fields

        when(transactionRepository.findByTransactionId("txn-001")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);
        when(transactionMapper.toTransactionResponseVersion1(updatedTransaction)).thenReturn(response);

        // Act
        TransactionResponse.Version1 result = transactionApiService.updateTransaction("txn-001", request);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).findByTransactionId("txn-001");
        verify(transactionRepository).save(transaction);
        verify(transactionMapper).toTransactionResponseVersion1(updatedTransaction);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent transaction")
    void testUpdateTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findByTransactionId("txn-001")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionApiService.updateTransaction("txn-001", request));
        assertEquals("Transaction not found with transactionId: txn-001", exception.getMessage());
        verify(transactionRepository).findByTransactionId("txn-001");
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void testDeleteTransaction() {
        // Arrange
        when(transactionRepository.findByTransactionId("txn-001")).thenReturn(Optional.of(transaction));

        // Act
        transactionApiService.deleteTransaction("txn-001");

        // Assert
        verify(transactionRepository).findByTransactionId("txn-001");
        verify(transactionRepository).delete(transaction);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent transaction")
    void testDeleteTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findByTransactionId("txn-001")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionApiService.deleteTransaction("txn-001"));
        assertEquals("Transaction not found with transactionId: txn-001", exception.getMessage());
        verify(transactionRepository).findByTransactionId("txn-001");
        verifyNoMoreInteractions(transactionRepository);
    }
}
