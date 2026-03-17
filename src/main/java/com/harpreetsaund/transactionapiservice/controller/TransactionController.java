package com.harpreetsaund.transactionapiservice.controller;

import com.harpreetsaund.transactionapiclient.dto.TransactionRequest;
import com.harpreetsaund.transactionapiclient.dto.TransactionResponse;
import com.harpreetsaund.transactionapiservice.service.TransactionApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@Tag(name = "Transaction API", description = "API for managing banking transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionApiService transactionApiService;

    public TransactionController(TransactionApiService transactionApiService) {
        this.transactionApiService = transactionApiService;
    }

    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Creates a new transaction record in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.Version1.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or transaction already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<TransactionResponse.Version1> createTransaction(
            @Valid @RequestBody @Parameter(description = "Transaction creation request", required = true) TransactionRequest.Version1 request) {
        logger.info("Received request to create transaction with eventId: {}", request.getEventId());
        try {
            TransactionResponse.Version1 response = transactionApiService.createTransaction(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves a transaction by its system-generated ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.Version1.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<TransactionResponse.Version1> getTransactionById(
            @PathVariable @Parameter(description = "System-generated transaction ID", required = true) Long id) {
        logger.info("Received request to get transaction by id: {}", id);
        try {
            return transactionApiService.getTransactionById(id).map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error retrieving transaction by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get transaction by transaction ID", description = "Retrieves a transaction by its business transaction ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.Version1.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<TransactionResponse.Version1> getTransactionByTransactionId(
            @PathVariable @Parameter(description = "Business transaction ID", required = true) String transactionId) {
        logger.info("Received request to get transaction by transactionId: {}", transactionId);
        try {
            return transactionApiService.getTransactionByTransactionId(transactionId).map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error retrieving transaction by transactionId: {}", transactionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get transactions by account number", description = "Retrieves all transactions for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.Version1.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<List<TransactionResponse.Version1>> getTransactionsByAccountNumber(
            @PathVariable @Parameter(description = "Account number", required = true) String accountNumber) {
        logger.info("Received request to get transactions for account: {}", accountNumber);
        try {
            List<TransactionResponse.Version1> transactions = transactionApiService
                    .getTransactionsByAccountNumber(accountNumber);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error retrieving transactions for account: {}", accountNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieves all transactions in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.Version1.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<List<TransactionResponse.Version1>> getAllTransactions() {
        logger.info("Received request to get all transactions");
        try {
            List<TransactionResponse.Version1> transactions = transactionApiService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error retrieving all transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/transaction/{transactionId}")
    @Operation(summary = "Update transaction", description = "Updates an existing transaction by its transaction ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.Version1.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<TransactionResponse.Version1> updateTransaction(
            @PathVariable @Parameter(description = "Business transaction ID", required = true) String transactionId,
            @Valid @RequestBody @Parameter(description = "Transaction update request", required = true) TransactionRequest.Version1 request) {
        logger.info("Received request to update transaction with transactionId: {}", transactionId);
        try {
            TransactionResponse.Version1 response = transactionApiService.updateTransaction(transactionId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating transaction with transactionId: {}", transactionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/transaction/{transactionId}")
    @Operation(summary = "Delete transaction", description = "Deletes a transaction by its transaction ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable @Parameter(description = "Business transaction ID", required = true) String transactionId) {
        logger.info("Received request to delete transaction with transactionId: {}", transactionId);
        try {
            transactionApiService.deleteTransaction(transactionId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete transaction: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting transaction with transactionId: {}", transactionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
