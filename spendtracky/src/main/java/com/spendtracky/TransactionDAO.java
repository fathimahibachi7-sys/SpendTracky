
package com.spendtracky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void addTransaction(Transaction transaction)
            throws SQLException {

        String sql = """
                INSERT INTO transactions
                (type, amount, description, category)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, transaction.getType());
            statement.setBigDecimal(2, transaction.getAmount());
            statement.setString(3, transaction.getDescription());
            statement.setString(4, transaction.getCategory());

            statement.executeUpdate();
        }
    }

    public List<Transaction> getAllTransactions()
            throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = """
                SELECT id, type, amount, description,
                       category, transaction_date
                FROM transactions
                ORDER BY transaction_date DESC
                """;

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Timestamp timestamp =
                        resultSet.getTimestamp("transaction_date");

                transactions.add(new Transaction(
                        resultSet.getInt("id"),
                        resultSet.getString("type"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getString("description"),
                        resultSet.getString("category"),
                        timestamp == null ? null : timestamp.toLocalDateTime()
                ));
            }
        }

        return transactions;
    }

    public BigDecimal getTotalByType(String type)
            throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM transactions
                WHERE type = ?
                """;

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, type);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1);
                }
            }
        }

        return BigDecimal.ZERO;
    }
}