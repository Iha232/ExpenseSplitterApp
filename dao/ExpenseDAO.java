package dao;

import model.Expense;
import model.ExpenseSplit;
import model.Balance;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public int addExpense(Expense expense) throws SQLException {
        String insertExpenseSql = "INSERT INTO Expenses (payer_id, description, total_amount, expense_date) VALUES (?, ?, ?, ?)";
        String insertSplitSql = "INSERT INTO ExpenseSplits (expense_id, member_id, share_amount, is_settled) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement expenseStmt = conn.prepareStatement(insertExpenseSql, Statement.RETURN_GENERATED_KEYS)) {
            expenseStmt.setInt(1, expense.getPayerId());
            expenseStmt.setString(2, expense.getDescription());
            expenseStmt.setBigDecimal(3, expense.getTotalAmount());
            expenseStmt.setDate(4, Date.valueOf(expense.getExpenseDate()));

            int affectedRows = expenseStmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("Creating expense failed, no rows affected.");
            }

            int expenseId;
            try (ResultSet generatedKeys = expenseStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expenseId = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    throw new SQLException("Creating expense failed, no ID obtained.");
                }
            }

            List<ExpenseSplit> splits = expense.getSplits();
            if (splits != null && !splits.isEmpty()) {
                try (PreparedStatement splitStmt = conn.prepareStatement(insertSplitSql)) {
                    for (ExpenseSplit split : splits) {
                        splitStmt.setInt(1, expenseId);
                        splitStmt.setInt(2, split.getMemberId());
                        splitStmt.setBigDecimal(3, split.getShareAmount());
                        splitStmt.setBoolean(4, split.isSettled());
                        splitStmt.addBatch();
                    }
                    splitStmt.executeBatch();
                }
            }

            conn.commit();
            return expenseId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
            SELECT e.expense_id, e.payer_id, e.description, e.total_amount, e.expense_date,
                   r.name as payer_name
            FROM Expenses e
            JOIN Roommates r ON e.payer_id = r.member_id
            ORDER BY e.expense_date DESC, e.expense_id DESC
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Expense e = new Expense();
                e.setExpenseId(rs.getInt("expense_id"));
                e.setPayerId(rs.getInt("payer_id"));
                e.setDescription(rs.getString("description"));
                e.setTotalAmount(rs.getBigDecimal("total_amount"));
                e.setExpenseDate(rs.getDate("expense_date").toLocalDate());
                e.setPayerName(rs.getString("payer_name"));
                expenses.add(e);
            }
        }
        return expenses;
    }

    public List<ExpenseSplit> getSplitsForExpense(int expenseId) throws SQLException {
        List<ExpenseSplit> splits = new ArrayList<>();
        String sql = """
            SELECT es.split_id, es.expense_id, es.member_id, es.share_amount, es.is_settled,
                   r.name as member_name
            FROM ExpenseSplits es
            JOIN Roommates r ON es.member_id = r.member_id
            WHERE es.expense_id = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, expenseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExpenseSplit split = new ExpenseSplit();
                    split.setSplitId(rs.getInt("split_id"));
                    split.setExpenseId(rs.getInt("expense_id"));
                    split.setMemberId(rs.getInt("member_id"));
                    split.setShareAmount(rs.getBigDecimal("share_amount"));
                    split.setSettled(rs.getBoolean("is_settled"));
                    split.setMemberName(rs.getString("member_name"));
                    splits.add(split);
                }
            }
        }
        return splits;
    }

    public List<Balance> getBalances() throws SQLException {
        List<Balance> balances = new ArrayList<>();
        String sql = """
            SELECT r.member_id, r.name, r.upi_id,
                   COALESCE(paid.total_paid, 0) as total_paid,
                   COALESCE(owed.total_owed, 0) as total_owed
            FROM Roommates r
            LEFT JOIN (
                SELECT payer_id as member_id, SUM(total_amount) as total_paid
                FROM Expenses
                GROUP BY payer_id
            ) paid ON r.member_id = paid.member_id
            LEFT JOIN (
                SELECT es.member_id, SUM(es.share_amount) as total_owed
                FROM ExpenseSplits es
                WHERE es.is_settled = FALSE
                GROUP BY es.member_id
            ) owed ON r.member_id = owed.member_id
            ORDER BY r.name
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Balance b = new Balance();
                b.setMemberId(rs.getInt("member_id"));
                b.setMemberName(rs.getString("name"));
                b.setUpiId(rs.getString("upi_id"));
                b.setTotalPaid(rs.getBigDecimal("total_paid"));
                b.setTotalOwed(rs.getBigDecimal("total_owed"));
                b.setNetBalance(b.getTotalPaid().subtract(b.getTotalOwed()));
                balances.add(b);
            }
        }
        return balances;
    }

    public boolean settleMemberDues(int memberId) throws SQLException {
        String sql = """
            UPDATE ExpenseSplits
            SET is_settled = TRUE
            WHERE member_id = ? AND is_settled = FALSE
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteExpense(int expenseId) throws SQLException {
        String sql = "DELETE FROM Expenses WHERE expense_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, expenseId);
            return stmt.executeUpdate() > 0;
        }
    }
}