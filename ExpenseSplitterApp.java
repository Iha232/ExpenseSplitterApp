import controller.ExpenseController;
import view.MainFrame;

import javax.swing.*;

/**
 * Apartment/Hostel Expense Splitter & Settlement Manager
 * 
 * A Java Swing desktop application for managing shared expenses among roommates.
 * 
 * Features:
 * - Add roommates with name, phone, and UPI ID
 * - Record expenses with description, amount, payer, and participants
 * - Automatic equal splitting of expenses
 * - View settlement dashboard with net balances
 * - Transaction ledger with history
 * - One-click settlement of dues
 * - MySQL database with JDBC
 * 
 * Database Schema:
 * - Roommates(member_id, name, phone, upi_id)
 * - Expenses(expense_id, payer_id, description, total_amount, expense_date)
 * - ExpenseSplits(split_id, expense_id, member_id, share_amount, is_settled)
 * 
 * @author Java Desktop Developer
 * @version 1.0
 */
public class ExpenseSplitterApp {

    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize controller (sets up database connection and tables)
                ExpenseController controller = new ExpenseController();

                // Create and show main frame
                MainFrame frame = new MainFrame(controller);
                frame.setVisible(true);

                // Add shutdown hook to close database connection
                Runtime.getRuntime().addShutdownHook(new Thread(controller::shutdown));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage() +
                                "\n\nPlease ensure MySQL is running and database 'expense_db' exists.\n" +
                                "Connection: jdbc:mysql://localhost:3306/expense_db\n" +
                                "User: root, Password: password",
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}