# Apartment/Hostel Expense Splitter & Settlement Manager

A production-ready Java Swing desktop application for managing shared expenses among roommates/hostel mates. Built with clean MVC architecture, MySQL database via pure JDBC, and a modern Swing GUI.

## Features

- **Roommate Management**: Add/edit/delete roommates with name, phone, and UPI ID
- **Expense Tracking**: Record expenses with description, amount, payer, and participants
- **Automatic Splitting**: Equal division of expenses among selected participants
- **Settlement Dashboard**: Real-time balances showing who owes whom (color-coded)
- **Transaction Ledger**: Complete history with delete capability
- **One-Click Settlement**: Mark member's dues as settled instantly
- **Data Persistence**: MySQL database with automatic schema initialization

## Tech Stack

- **Language**: Java 17+
- **UI**: Java Swing (pure standard library, no external UI frameworks)
- **Database**: MySQL 8.0+
- **Database Access**: Pure JDBC (DriverManager, PreparedStatement, batch operations)
- **Architecture**: Model-View-Controller (MVC)
- **Build**: Maven (optional) or manual javac

## Database Schema

```sql
-- Auto-created on first run
CREATE TABLE Roommates (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    upi_id VARCHAR(50)
);

CREATE TABLE Expenses (
    expense_id INT AUTO_INCREMENT PRIMARY KEY,
    payer_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    expense_date DATE NOT NULL,
    FOREIGN KEY (payer_id) REFERENCES Roommates(member_id)
);

CREATE TABLE ExpenseSplits (
    split_id INT AUTO_INCREMENT PRIMARY KEY,
    expense_id INT NOT NULL,
    member_id INT NOT NULL,
    share_amount DECIMAL(10,2) NOT NULL,
    is_settled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (expense_id) REFERENCES Expenses(expense_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES Roommates(member_id)
);
```

## Project Structure

```
ExpenseSplitterApp/
├── ExpenseSplitterApp.java          # Main entry point
├── pom.xml                          # Maven build file
├── .gitignore
├── README.md
├── model/
│   ├── Roommate.java
│   ├── Expense.java
│   ├── ExpenseSplit.java
│   └── Balance.java
├── dao/
│   ├── DatabaseManager.java         # JDBC connection + schema init
│   ├── RoommateDAO.java             # Roommate CRUD
│   └── ExpenseDAO.java              # Expense operations + batch splits
├── controller/
│   └── ExpenseController.java       # Business logic & validation
└── view/
    ├── MainFrame.java               # JFrame with BorderLayout + JTabbedPane
    └── components/
        ├── RoommateFormPanel.java   # West: expense input form
        ├── DashboardPanel.java      # Center tab: balances table
        ├── LedgerPanel.java         # Center tab: transaction history
        └── BottomPanel.java         # South: settle up + refresh
```

## Prerequisites

- Java 17+ (`java -version`)
- MySQL Server 8.0+ running on localhost:3306
- MySQL JDBC Driver (downloaded automatically via Maven, or manually)

## Quick Start

### Option 1: With Maven (Recommended)

```bash
git clone https://github.com/Iha232/ExpenseSplitterApp.git
cd ExpenseSplitterApp

# Configure database credentials in dao/DatabaseManager.java if needed
# Default: jdbc:mysql://localhost:3306/expense_db, user: expense_user, pass: expense_pass

mvn compile exec:java
```

### Option 2: Manual Compilation

```bash
git clone https://github.com/Iha232/ExpenseSplitterApp.git
cd ExpenseSplitterApp

# Download MySQL Connector/J
curl -L -o mysql-connector-j-8.3.0.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar

# Compile
javac -cp "mysql-connector-j-8.3.0.jar" -d out -sourcepath . \
  ExpenseSplitterApp.java model/*.java dao/*.java controller/*.java view/*.java view/components/*.java

# Run
java -cp "out;mysql-connector-j-8.3.0.jar" ExpenseSplitterApp
```

## Database Setup

Before running, create the database and user:

```sql
-- Run as MySQL root
CREATE DATABASE expense_db;
CREATE USER 'expense_user'@'localhost' IDENTIFIED BY 'expense_pass';
GRANT ALL PRIVILEGES ON expense_db.* TO 'expense_user'@'localhost';
FLUSH PRIVILEGES;
```

Update `dao/DatabaseManager.java` if using different credentials:
```java
private static final String URL = "jdbc:mysql://localhost:3306/expense_db";
private static final String USER = "expense_user";
private static final String PASSWORD = "expense_pass";
```

## Usage Guide

1. **Add Roommates**: Fill name, phone, UPI ID → they appear in dropdowns
2. **Add Expense**: 
   - Enter description (e.g., "Groceries")
   - Enter amount (e.g., 1500)
   - Select who paid from dropdown
   - Check participants in checkbox list
   - Click "Add Expense"
3. **Dashboard Tab**: View each member's Total Paid, Total Owed, Net Balance
   - Green = receives money, Red = owes money
4. **Ledger Tab**: Full transaction history
   - Select row → "Delete Selected" to remove expense
5. **Bottom Panel**: 
   - Select member who owes → "Settle Up" → marks all their dues as paid
   - "Refresh All" reloads all tabs

## Key JDBC Operations Implemented

| Operation | Implementation |
|-----------|----------------|
| Schema Init | `CREATE TABLE IF NOT EXISTS` in `DatabaseManager.initializeDatabase()` |
| Batch Insert | `PreparedStatement.addBatch()` / `executeBatch()` for `ExpenseSplits` |
| Generated Keys | `Statement.RETURN_GENERATED_KEYS` to get `expense_id` after insert |
| Aggregation Query | `SUM(CASE...)` with `LEFT JOIN` for balance computation |
| Settlement Update | `UPDATE ExpenseSplits SET is_settled = TRUE WHERE member_id = ?` |
| Cascade Delete | `ON DELETE CASCADE` on `ExpenseSplits.expense_id` FK |

## Screenshots

*Add your screenshots here by placing images in a `screenshots/` folder and referencing them.*

## License

MIT License - Feel free to use and modify for personal or educational purposes.

## Author

Built as a demonstration of clean Java desktop development with MVC, Swing, and JDBC.