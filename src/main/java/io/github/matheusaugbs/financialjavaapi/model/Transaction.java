package io.github.matheusaugbs.financialjavaapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Transaction {

    private Long id;
    private String nsu;
    private String authorizationNumber;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private TransactionTypeEnum type;

    public Transaction(TransactionTypeEnum type) {
        this.type = type;
    }
}
