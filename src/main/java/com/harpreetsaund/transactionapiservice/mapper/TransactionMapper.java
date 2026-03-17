package com.harpreetsaund.transactionapiservice.mapper;

import com.harpreetsaund.transactionapiclient.dto.TransactionRequest;
import com.harpreetsaund.transactionapiclient.dto.TransactionResponse;
import com.harpreetsaund.transactionapiservice.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toTransaction(TransactionRequest.Version1 request) {
        Transaction transaction = new Transaction();
        transaction.setEventId(request.getEventId());
        transaction.setTransactionId(request.getTransactionId());
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setMerchantName(request.getMerchantName());
        transaction.setChannel(request.getChannel());
        transaction.setTransactionTimestamp(request.getTransactionTimestamp());
        transaction.setSourceSystem(request.getSourceSystem());
        transaction.setTopicName(request.getTopicName());

        return transaction;
    }

    public TransactionResponse.Version1 toTransactionResponseVersion1(Transaction transaction) {
        TransactionResponse.Version1 response = new TransactionResponse.Version1();
        response.setId(transaction.getId());
        response.setEventId(transaction.getEventId());
        response.setTransactionId(transaction.getTransactionId());
        response.setAccountNumber(transaction.getAccountNumber());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setMerchantName(transaction.getMerchantName());
        response.setChannel(transaction.getChannel());
        response.setTransactionTimestamp(transaction.getTransactionTimestamp());
        response.setSourceSystem(transaction.getSourceSystem());
        response.setTopicName(transaction.getTopicName());
        response.setCreatedAt(transaction.getCreatedAt());

        return response;
    }
}
