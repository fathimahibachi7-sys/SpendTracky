
package com.spendtracky;

import java.sql.Connection;
import java.sql.SQLException;

public class DBtest {

    public static void main(String[] args) {
        try (Connection connection = DBconnection.getConnection()) {
            System.out.println("SpendTracky connected to MySQL successfully!");
            System.out.println("Database: " +
                    connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println(e.getMessage());
        }
    }
}