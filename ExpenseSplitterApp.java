import controller.ExpenseController;
import view.AuthFrame;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Apartment/Hostel Expense Splitter & Settlement Manager
 * 
 * A Java Swing desktop application for managing shared expenses among roommates.
 * 
 * Features:
 * - User authentication (login/register)
 * - Add roommates with name, phone, and UPI ID
 * - Record expenses with description, amount, payer, and participants
 * - Automatic equal splitting of expenses
 * - View settlement dashboard with net balances
 * - Transaction ledger with history
 * - One-click settlement of dues
 * - MySQL database with JDBC
 * 
 * Database Schema:
 * - Users(user_id, username, password_hash, email)
 * - Roommates(member_id, user_id, name, phone, upi_id)
 * - Expenses(expense_id, user_id, payer_id, description, total_amount, expense_date)
 * - ExpenseSplits(split_id, expense_id, member_id, share_amount, is_settled)
 * 
 * @author Java Desktop Developer
 * @version 1.0
 */
public class ExpenseSplitterApp {

    public static void main(String[] args) {
        // Ensure we are not headless
        System.setProperty("java.awt.headless", "false");
        
        // Set default uncaught exception handler for EDT
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception in thread " + t.getName());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Unexpected error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        });

        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Starting ExpenseSplitterApp...");
                // Initialize controller (sets up database connection and tables)
                ExpenseController controller = new ExpenseController();
                System.out.println("Controller initialized.");

                // Create and show auth frame (login/register)
                AuthFrame authFrame = new AuthFrame(controller);
                System.out.println("AuthFrame created, making visible...");
                authFrame.setVisible(true);
                System.out.println("AuthFrame set visible.");

                // Add shutdown hook to close database connection
                Runtime.getRuntime().addShutdownHook(new Thread(controller::shutdown));

            } catch (Throwable e) {
                System.err.println("Fatal startup error:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage() +
                                "\n\nPlease ensure MySQL is running and database 'expense_db' exists.\n" +
                                "Connection: jdbc:mysql://localhost:3306/expense_db\n" +
                                "User: expense_user, Password: expense_pass",
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}