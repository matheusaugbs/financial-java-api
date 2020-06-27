package io.github.matheusaugbs.financialjavaapi.factory;

import io.github.matheusaugbs.financialjavaapi.model.Transaction;

public interface TransactionFactory {
    Transaction createTransaction(String type);
}
