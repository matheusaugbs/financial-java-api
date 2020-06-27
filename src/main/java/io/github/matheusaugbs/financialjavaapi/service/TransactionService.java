package io.github.matheusaugbs.financialjavaapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.matheusaugbs.financialjavaapi.constant.TransactionTypeEnum;
import io.github.matheusaugbs.financialjavaapi.factory.TransactionFactory;
import io.github.matheusaugbs.financialjavaapi.factory.impl.TransactionFactoryImpl;
import io.github.matheusaugbs.financialjavaapi.model.Transaction;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class TransactionService {

    private TransactionFactory factory;
    private List<Transaction> transactions;

    public void createTransactionFactory() {
        if (factory == null) {
            factory = new TransactionFactoryImpl();
        }
    }

    public void createTransactionList() {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
    }

    public boolean isJSONValid(String jsonInString) {
        try {
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private long parseId(JSONObject transaction) {
        return Long.valueOf((int) transaction.get("id"));
    }

    private BigDecimal parseAmount(JSONObject transaction) {
        return new BigDecimal((String) transaction.get("amount"));
    }

    private LocalDateTime parseTransactionDate(JSONObject transaction) {
        var transactionDate = (String) transaction.get("transactionDate");
        return ZonedDateTime.parse(transactionDate).toLocalDateTime();
    }

    public boolean isTransactionInFuture(Transaction transaction) {
        return transaction.getTransactionDate().isAfter(LocalDateTime.now());
    }

    private void setTransactionValues(JSONObject jsonTransaction, Transaction transaction) {

        String authorizationNumber = (String) jsonTransaction.get("authorizationNumber");
        String nsu = (String) jsonTransaction.get("nsu");

        transaction.setAmount(jsonTransaction.get("amount") != null ? parseAmount(jsonTransaction) : transaction.getAmount());
        transaction.setTransactionDate(jsonTransaction.get("transactionDate") != null ? parseTransactionDate(jsonTransaction) : transaction.getTransactionDate());
        transaction.setAuthorizationNumber(TransactionTypeEnum.CARD == transaction.getType() ? authorizationNumber : null);
        transaction.setNsu(nsu != null ? nsu : transaction.getNsu());
    }

    public Transaction create(JSONObject jsonTransaction) {

        createTransactionFactory();

        Transaction transaction = factory.createTransaction((String) jsonTransaction.get("type"));
        transaction.setId(parseId(jsonTransaction));
        setTransactionValues(jsonTransaction, transaction);

        return transaction;
    }

    public void add(Transaction transaction) {
        createTransactionList();
        transactions.add(transaction);
    }

    public List<Transaction> find() {
        createTransactionList();
        return transactions;
    }

    public Transaction findById(long id) {
        return transactions.stream().filter(t -> id == t.getId()).collect(Collectors.toList()).get(0);
    }

    public boolean delete() {
        return transactions.isEmpty();
    }

    public void clearObjects() {
        transactions = null;
        factory = null;
    }
}
