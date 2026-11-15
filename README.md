# Apartment/Hostel Expense Splitter & Settlement Manager

A production-ready Java Swing desktop application for managing shared expenses among roommates/hostel mates. Built with clean MVC architecture, MySQL database via pure JDBC, and a modern Swing GUI.

**This project demonstrates complete Java Units 3 & 4 syllabus integration** - all concepts from Packages/Interfaces, Exception Handling, I/O, Collections, String Handling, Event Handling, and Multithreading are implemented as practical features.

## Features

### Core Features
- **User Authentication**: Register/Login with SHA-256 password hashing
- **Roommate Management**: Add/edit/delete roommates with name, phone, and UPI ID
- **Expense Tracking**: Record expenses with description, amount, payer, and participants
- **Automatic Splitting**: Equal division of expenses among selected participants
- **Settlement Dashboard**: Real-time balances showing who owes whom (color-coded)
- **Transaction Ledger**: Complete history with delete capability
- **One-Click Settlement**: Mark member's dues as settled instantly
- **Data Persistence**: MySQL database with automatic schema initialization

### Enhanced Features (Unit 3 & 4 Integration)
- **Search Expenses**: Real-time search by description, payer, date, or amount (Ctrl+F)
- **Export Expense Report**: Formatted text reports with PrintWriter (Ctrl+E)
- **Export Simple Summary**: Quick summary export
- **Backup Data**: Full data backup with ObjectOutputStream/Serializable (Ctrl+B)
- **Restore Data**: Restore from backup with ObjectInputStream (Ctrl+R)
- **String vs StringBuffer Demo**: Live performance comparison tool
- **Background Processing**: Non-blocking UI with SwingWorker/ExecutorService
- **Keyboard Shortcuts**: Full accelerator key support
- **Menu Bar & Toolbar**: Professional desktop app experience

## Tech Stack

- **Language**: Java 17+
- **UI**: Java Swing (pure standard library, no external UI frameworks)
- **Database**: MySQL 8.0+
- **Database Access**: Pure JDBC (DriverManager, PreparedStatement, batch operations)
- **Architecture**: Model-View-Controller (MVC) with Interface-based design
- **Build**: Maven (optional) or manual javac

## Database Schema

```sql
-- Auto-created on first run
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
├── JAVA_SYLLABUS_IMPLEMENTATION.md  # Detailed syllabus mapping
├── model/
│   ├── User.java
│   ├── Roommate.java
│   ├── Expense.java
│   ├── ExpenseSplit.java
│   └── Balance.java
├── dao/
│   ├── DatabaseManager.java         # JDBC connection + schema init
│   ├── UserDAO.java                 # User CRUD + auth
│   ├── RoommateDAO.java             # Roommate CRUD
│   └── ExpenseDAO.java              # Expense operations + batch splits
├── controller/
│   └── ExpenseController.java       # Business logic & validation
├── interfaces/
│   ├── ExpenseRepository.java       # Expense data access contract
│   ├── RoommateRepository.java      # Roommate data access contract
│   ├── Exportable.java              # Export capability interface
│   └── NotificationListener.java    # Event listener interface
├── exceptions/
│   ├── InvalidExpenseException.java
│   ├── InvalidRoommateException.java
│   ├── DuplicateUsernameException.java
│   ├── DatabaseConnectionException.java
│   └── InsufficientParticipantException.java
├── utils/
│   ├── StringUtils.java             # 20+ string operations
│   ├── FileUtils.java               # File/directory operations
│   └── ValidationUtils.java         # Validation with custom exceptions
├── services/
│   ├── ExportService.java           # PrintWriter + StringBuffer reports
│   ├── BackupService.java           # ObjectOutputStream/InputStream
│   └── SearchService.java           # Search with Collections/Iterator
├── threads/
│   ├── TaskExecutor.java            # ExecutorService + thread pools
│   ├── BackgroundTask.java          # SwingWorker base class
│   └── ExpenseBackgroundTasks.java  # Domain-specific background tasks
├── events/
│   ├── ApplicationEvent.java        # Base event class
│   ├── ExpenseEvent.java            # Expense events
│   ├── RoommateEvent.java           # Roommate events
│   ├── BalanceEvent.java            # Balance events
│   └── EventManager.java            # Central event dispatcher
├── view/
│   ├── AuthFrame.java               # Login/Register window
│   ├── MainFrame.java               # Main app with menu/toolbar/search
│   └── components/
│       ├── AuthPanel.java           # Login/Register forms
│       ├── RoommateFormPanel.java   # Expense input + roommate mgmt
│       ├── DashboardPanel.java      # Balances table
│       ├── LedgerPanel.java         # Transaction history + search
│       └── BottomPanel.java         # Settle up + refresh
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
  ExpenseSplitterApp.java model/*.java dao/*.java controller/*.java view/*.java view/components/*.java interfaces/*.java exceptions/*.java utils/*.java services/*.java threads/*.java events/*.java

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

### Default Credentials (Pre-seeded)
| Username | Password |
|----------|----------|
| arpita   | arpita@2007 |
| sujal    | sujal@2007 |
| iha      | iha@2007 |
| vedika   | vedika@2007 |

### Keyboard Shortcuts
| Shortcut | Action |
|----------|--------|
| Ctrl+E | Export Expense Report |
| Ctrl+B | Backup Data |
| Ctrl+R | Restore Data |
| Ctrl+F | Search Expenses |
| F5 | Refresh All |
| Ctrl+Q | Exit |

### Workflow
1. **Register/Login**: Create account or login with existing credentials
2. **Add Roommates**: Click "Add Roommate" (blue button) → fill name, phone, UPI → Save
3. **Add Expense**: 
   - Enter description (e.g., "Groceries")
   - Enter amount (e.g., 1500)
   - Select who paid from dropdown
   - Check participants in "Split Between" dropdown
   - Click "Add Expense"
4. **Dashboard Tab**: View each member's Total Paid, Total Owed, Net Balance
   - Green = receives money, Red = owes money
5. **Ledger Tab**: Full transaction history with search
   - Use search bar (Ctrl+F) to filter by description, payer, date, amount
   - Select row → "Delete Selected" to remove expense
6. **Bottom Panel**: 
   - Select member who owes → "Settle Up" → marks all their dues as paid
   - "Refresh All" reloads all tabs
7. **File Menu**: Export reports, backup/restore data
8. **Tools Menu**: Settle dues, String vs StringBuffer demo
9. **Help Menu**: About, Syllabus Implementation info
10. **Logout**: Returns to login screen

## Key JDBC Operations Implemented

| Operation | Implementation |
|-----------|----------------|
| Schema Init | `CREATE TABLE IF NOT EXISTS` in `DatabaseManager.initializeDatabase()` |
| Batch Insert | `PreparedStatement.addBatch()` / `executeBatch()` for `ExpenseSplits` |
| Generated Keys | `Statement.RETURN_GENERATED_KEYS` to get `expense_id` after insert |
| Aggregation Query | `SUM(CASE...)` with `LEFT JOIN` for balance computation |
| Settlement Update | `UPDATE ExpenseSplits SET is_settled = TRUE WHERE member_id = ?` |
| Cascade Delete | `ON DELETE CASCADE` on `ExpenseSplits.expense_id` FK |

## Syllabus Concepts Demonstrated

This project implements **ALL** concepts from Java Units 3 & 4:

### Unit 3 - Packages and Interfaces
- ✅ Package organization (10 packages)
- ✅ CLASSPATH configuration (Maven + manual)
- ✅ Access protection (public/private/protected/default)
- ✅ Interfaces (4 interfaces with implementations)
- ✅ Custom exceptions (5 checked exceptions)
- ✅ Exception handling (try/catch/finally, try-with-resources, throw/throws)
- ✅ PrintWriter for report generation
- ✅ Object Streams (ObjectOutputStream/ObjectInputStream)
- ✅ Serialization (BackupData implements Serializable)
- ✅ File I/O (FileUtils with 15+ operations)

### Unit 4 - Java Library
- ✅ String Handling (20+ operations in StringUtils)
- ✅ StringBuffer vs String (performance demo in ExportService)
- ✅ Collections Framework (ArrayList, LinkedHashMap, Iterator, Streams)
- ✅ Event Handling (Delegation Event Model with custom events)
- ✅ Multithreading (ExecutorService, SwingWorker, synchronization)
- ✅ Safe thread cancellation (Thread.interrupt, no deprecated methods)

## Screenshots

*Add your screenshots here by placing images in a `screenshots/` folder and referencing them.*

## License

MIT License - Feel free to use and modify for personal or educational purposes.

## Author

Built as a demonstration of clean Java desktop development with MVC, Swing, and JDBC. Complete implementation of Java Units 3 & 4 syllabus concepts for SRM Institute of Science and Technology.

## Academic Documentation

See [JAVA_SYLLABUS_IMPLEMENTATION.md](JAVA_SYLLABUS_IMPLEMENTATION.md) for comprehensive documentation mapping every syllabus topic to its implementation.