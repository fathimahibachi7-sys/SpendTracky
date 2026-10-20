
package com.spendtracky;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private String type;
    private BigDecimal amount;
    private String description;
    private String category;
    private LocalDateTime transactionDate;

    public Transaction(String type, BigDecimal amount,
                       String description, String category) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public Transaction(int id, String type, BigDecimal amount,
                       String description, String category,
                       LocalDateTime transactionDate) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.transactionDate = transactionDate;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}