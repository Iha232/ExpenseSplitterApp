package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_db";
    private static final String USER = "expense_user";
    private static final String PASSWORD = "expense_pass";

    private static Connection connection;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            initializeDatabase(connection);
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        String createRoommatesTable = """
            CREATE TABLE IF NOT EXISTS Roommates (
                member_id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                phone VARCHAR(20),
                upi_id VARCHAR(50)
            )
        """;

        String createExpensesTable = """
            CREATE TABLE IF NOT EXISTS Expenses (
                expense_id INT AUTO_INCREMENT PRIMARY KEY,
                payer_id INT NOT NULL,
                description VARCHAR(255) NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                expense_date DATE NOT NULL,
                FOREIGN KEY (payer_id) REFERENCES Roommates(member_id)
            )
        """;

        String createExpenseSplitsTable = """
            CREATE TABLE IF NOT EXISTS ExpenseSplits (
                split_id INT AUTO_INCREMENT PRIMARY KEY,
                expense_id INT NOT NULL,
                member_id INT NOT NULL,
                share_amount DECIMAL(10,2) NOT NULL,
                is_settled BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (expense_id) REFERENCES Expenses(expense_id) ON DELETE CASCADE,
                FOREIGN KEY (member_id) REFERENCES Roommates(member_id)
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createRoommatesTable);
            stmt.execute(createExpensesTable);
            stmt.execute(createExpenseSplitsTable);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}