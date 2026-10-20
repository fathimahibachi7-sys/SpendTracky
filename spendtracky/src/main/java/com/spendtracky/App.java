package com.spendtracky;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;

public class App {
    private static final TransactionDAO transactionDAO = new TransactionDAO();
    

    private static double totalIncome = 0.0;
    private static double totalExpenses = 0.0;
    private static JFrame dashboardFrame;

    private static final Color BACKGROUND = new Color(15, 27, 46);
    private static final Color CARD = new Color(29, 45, 68);
    private static final Color TEXT = new Color(245, 248, 255);
    private static final Color MUTED = new Color(170, 190, 215);
    private static final Color ACCENT = new Color(76, 190, 210);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::showWelcome);
    }

    private static void showWelcome() {
        JFrame frame = new JFrame("SpendTracky");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("SpendTracky");
        title.setFont(new Font("SansSerif", Font.BOLD, 38));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Think Before You Spend.");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 17));
        tagline.setForeground(ACCENT);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton start = new JButton("Open Dashboard");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setFocusPainted(false);

        start.addActionListener(e -> {
            frame.dispose();
            showDashboard();
        });

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(tagline);
        panel.add(Box.createRigidArea(new Dimension(0, 35)));
        panel.add(start);
        panel.add(Box.createVerticalGlue());

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showDashboard() {
        try{
            totalIncome = transactionDAO.getTotalByType("INCOME").doubleValue();
            totalExpenses = transactionDAO.getTotalByType("EXPENSE").doubleValue();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error fetching data from the database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        JFrame frame = new JFrame("SpendTracky | Dashboard");
        dashboardFrame = frame;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 680);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(28, 30, 28, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Your Financial Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Make every rupee count.");
        subtitle.setForeground(MUTED);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createRigidArea(new Dimension(0, 7)));
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);

        JButton addExpense = new JButton("+ Add Expense");
        addExpense.addActionListener(e -> showAddExpenseForm());
        header.add(addExpense, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setOpaque(false);

        cards.add(createCard(
                "AVAILABLE BALANCE",
                formatCurrency(totalIncome - totalExpenses),
                "Your current balance"
        ));

        cards.add(createCard(
                "TOTAL INCOME",
                formatCurrency(totalIncome),
                "+ Add Income"
        ));

        cards.add(createCard(
                "TOTAL EXPENSES",
                formatCurrency(totalExpenses),
                "+ Add Expense"
        ));

        cards.add(createCard(
                "SAVINGS GOAL",
                "Not set",
                "Create a goal to track progress"
        ));

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JLabel simulatorTitle = new JLabel("Before You Buy");
        simulatorTitle.setForeground(TEXT);
        simulatorTitle.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel simulatorDescription = new JLabel(
                "Check how a possible purchase could affect your budget."
        );
        simulatorDescription.setForeground(MUTED);

        JButton simulate = new JButton("Simulate a Purchase");
        simulate.addActionListener(e -> simulatePurchase());

        bottom.add(simulatorTitle);
        bottom.add(Box.createRigidArea(new Dimension(0, 8)));
        bottom.add(simulatorDescription);
        bottom.add(Box.createRigidArea(new Dimension(0, 16)));
        bottom.add(simulate);

        main.add(header, BorderLayout.NORTH);
        main.add(cards, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        frame.setContentPane(main);
        frame.setVisible(true);
    }

    private static JPanel createCard(
            String heading,
            String value,
            String description) {

        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel label = new JLabel(heading);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel amount = new JLabel(value);
        amount.setForeground(TEXT);
        amount.setFont(new Font("SansSerif", Font.BOLD, 27));

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("SansSerif", Font.PLAIN, 12));

        if (description.equals("+ Add Income")
                || description.equals("+ Add Expense")) {

            detail.setForeground(new Color(80, 190, 255));
            detail.setCursor(new Cursor(Cursor.HAND_CURSOR));

            detail.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (description.equals("+ Add Income")) {
                        showAddIncomeForm();
                    } else {
                        showAddExpenseForm();
                    }
                }
            });

        } else {
            detail.setForeground(MUTED);
        }

        card.add(label);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(amount);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(detail);

        return card;
    }

    private static void showAddIncomeForm() {
        String input = JOptionPane.showInputDialog(
                dashboardFrame,
                "Enter income amount (₹):"
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            double amount = Double.parseDouble(input.trim());

            if (!Double.isFinite(amount) || amount <= 0) {
                throw new NumberFormatException();
            }

            try{
                Transaction incomeTransaction = new Transaction(
                        "INCOME",
                        new BigDecimal(amount),
                        "Manual income entry",
                        "General"
                );
                transactionDAO.addTransaction(incomeTransaction);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        dashboardFrame,
                        "Error saving income to the database:\n" + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    dashboardFrame,
                    "Income of " + formatCurrency(amount) + " recorded.",
                    "Income Recorded",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    dashboardFrame,
                    "Please enter a valid positive amount.",
                    "Invalid Amount",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private static void showAddExpenseForm() {
        String input = JOptionPane.showInputDialog(
                dashboardFrame,
                "Enter expense amount (₹):"
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            double amount = Double.parseDouble(input.trim());

            if (!Double.isFinite(amount) || amount <= 0) {
                throw new NumberFormatException();
            }

            try{
                transactionDAO.addTransaction(new Transaction(
                        "EXPENSE",
                        java.math.BigDecimal.valueOf(amount),
                        "Expense entry",
                        "General"
                )
            );
            refreshDashboard();
            } catch (java.sql.SQLException e) {
                JOptionPane.showMessageDialog(
                        dashboardFrame,
                        "Error saving expense to the database:\n" + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    dashboardFrame,
                    "Expense of " + formatCurrency(amount) + " recorded.",
                    "Expense Recorded",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    dashboardFrame,
                    "Please enter a valid positive amount.",
                    "Invalid Amount",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private static void refreshDashboard() {
        if (dashboardFrame != null) {
            dashboardFrame.dispose();
        }
        showDashboard();
    }

    private static void simulatePurchase() {
        String input = JOptionPane.showInputDialog(
                dashboardFrame,
                "What is the price of the item (₹)?"
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            double amount = Double.parseDouble(input.trim());

            if (!Double.isFinite(amount) || amount <= 0) {
                throw new NumberFormatException();
            }

            double balance = totalIncome - totalExpenses;
            double remaining = balance - amount;

            String message;

            if (remaining < 0) {
                message = "This purchase would exceed your available balance.";
            } else {
                message = "You can afford this purchase based on your recorded balance.";
            }

            JOptionPane.showMessageDialog(
                    dashboardFrame,
                    String.format(
                            "Purchase price: %s%n"
                                    + "Current balance: %s%n"
                                    + "Balance after purchase: %s%n%n"
                                    + "%s%n%n"
                                    + "This is only a simulation. "
                                    + "Your real totals have not changed.",
                            formatCurrency(amount),
                            formatCurrency(balance),
                            formatCurrency(remaining),
                            message
                    ),
                    "Purchase Simulation",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    dashboardFrame,
                    "Enter a valid positive amount.",
                    "Invalid Amount",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }
}