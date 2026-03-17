package com.harpreetsaund.transactionapiservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harpreetsaund.transactionapiclient.dto.TransactionRequest;
import com.harpreetsaund.transactionapiclient.dto.TransactionResponse;
import com.harpreetsaund.transactionapiservice.service.TransactionApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@DisplayName("TransactionController Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionApiService transactionApiService;

    private ObjectMapper objectMapper;

    private TransactionRequest.Version1 request;
    private TransactionResponse.Version1 response;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    void testCreateTransaction() throws Exception {
        // Arrange
        when(transactionApiService.createTransaction(any(TransactionRequest.Version1.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.eventId").value("evt-123"));

        verify(transactionApiService).createTransaction(any(TransactionRequest.Version1.class));
    }

    @Test
    @DisplayName("Should return bad request when create transaction fails")
    void testCreateTransaction_BadRequest() throws Exception {
        // Arrange
        when(transactionApiService.createTransaction(any(TransactionRequest.Version1.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        // Act & Assert
        mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());

        verify(transactionApiService).createTransaction(any(TransactionRequest.Version1.class));
    }

    @Test
    @DisplayName("Should get transaction by id")
    void testGetTransactionById() throws Exception {
        // Arrange
        when(transactionApiService.getTransactionById(1L)).thenReturn(Optional.of(response));

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/{id}", 1L)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(1L));

        verify(transactionApiService).getTransactionById(1L);
    }

    @Test
    @DisplayName("Should return not found when transaction not found by id")
    void testGetTransactionById_NotFound() throws Exception {
        // Arrange
        when(transactionApiService.getTransactionById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/{id}", 1L)).andExpect(status().isNotFound());

        verify(transactionApiService).getTransactionById(1L);
    }

    @Test
    @DisplayName("Should get transaction by transactionId")
    void testGetTransactionByTransactionId() throws Exception {
        // Arrange
        when(transactionApiService.getTransactionByTransactionId("txn-001")).thenReturn(Optional.of(response));

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/transaction/{transactionId}", "txn-001")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value("txn-001"));

        verify(transactionApiService).getTransactionByTransactionId("txn-001");
    }

    @Test
    @DisplayName("Should get transactions by account number")
    void testGetTransactionsByAccountNumber() throws Exception {
        // Arrange
        when(transactionApiService.getTransactionsByAccountNumber("ACC-12345")).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/account/{accountNumber}", "ACC-12345")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-12345"));

        verify(transactionApiService).getTransactionsByAccountNumber("ACC-12345");
    }

    @Test
    @DisplayName("Should get all transactions")
    void testGetAllTransactions() throws Exception {
        // Arrange
        when(transactionApiService.getAllTransactions()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/v1/transactions")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$[0].id").value(1L));

        verify(transactionApiService).getAllTransactions();
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void testUpdateTransaction() throws Exception {
        // Arrange
        when(transactionApiService.updateTransaction(eq("txn-001"), any(TransactionRequest.Version1.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/transactions/transaction/{transactionId}", "txn-001")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value("txn-001"));

        verify(transactionApiService).updateTransaction(eq("txn-001"), any(TransactionRequest.Version1.class));
    }

    @Test
    @DisplayName("Should return bad request when update transaction fails")
    void testUpdateTransaction_BadRequest() throws Exception {
        // Arrange
        when(transactionApiService.updateTransaction(eq("txn-001"), any(TransactionRequest.Version1.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        // Act & Assert
        mockMvc.perform(put("/v1/transactions/transaction/{transactionId}", "txn-001")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(transactionApiService).updateTransaction(eq("txn-001"), any(TransactionRequest.Version1.class));
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void testDeleteTransaction() throws Exception {
        // Arrange
        doNothing().when(transactionApiService).deleteTransaction("txn-001");

        // Act & Assert
        mockMvc.perform(delete("/v1/transactions/transaction/{transactionId}", "txn-001"))
                .andExpect(status().isNoContent());

        verify(transactionApiService).deleteTransaction("txn-001");
    }

    @Test
    @DisplayName("Should return not found when delete transaction fails")
    void testDeleteTransaction_NotFound() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Not found")).when(transactionApiService).deleteTransaction("txn-001");

        // Act & Assert
        mockMvc.perform(delete("/v1/transactions/transaction/{transactionId}", "txn-001"))
                .andExpect(status().isNotFound());

        verify(transactionApiService).deleteTransaction("txn-001");
    }
}
