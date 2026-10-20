
package com.spendtracky;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransactionTest {

    public static void main(String[] args) {
        TransactionDAO dao = new TransactionDAO();

        try {
            Transaction transaction = new Transaction(
                    "EXPENSE",
                    new BigDecimal("150.00"),
                    "Test transaction",
                    "Food"
            );

            dao.addTransaction(transaction);

            System.out.println("Transaction saved successfully!");

            System.out.println("Total expenses: ₹"
                    + dao.getTotalByType("EXPENSE"));

            System.out.println("All transactions:");

            for (Transaction item : dao.getAllTransactions()) {
                System.out.println(
                        item.getId() + " | "
                        + item.getType() + " | ₹"
                        + item.getAmount() + " | "
                        + item.getDescription() + " | "
                        + item.getCategory()
                );
            }

        } catch (SQLException e) {
            System.out.println("Transaction test failed:");
            System.out.println(e.getMessage());
        }
    }
}