package interfaces;

import model.Expense;
import model.ExpenseSplit;
import model.Balance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for expense repository operations.
 * 
 * This interface demonstrates the concept of abstraction in Java - defining a contract
 * for expense data access without specifying implementation details.
 * 
 * Syllabus Topics Covered:
 * - Interface definition
 * - Abstraction
 * - Contract-based programming
 * - Polymorphism through interface implementation
 * - Method signatures without implementation
 * 
 * @see dao.ExpenseDAO
 */
public interface ExpenseRepository {
    
    /**
     * Adds a new expense with its splits.
     * 
     * @param expense the expense to add
     * @return the generated expense ID
     * @throws Exception if database operation fails
     */
    int addExpense(Expense expense) throws Exception;
    
    /**
     * Retrieves all expenses.
     * 
     * @return list of all expenses
     * @throws Exception if database operation fails
     */
    List<Expense> getAllExpenses() throws Exception;
    
    /**
     * Retrieves expense splits for a specific expense.
     * 
     * @param expenseId the expense ID
     * @return list of expense splits
     * @throws Exception if database operation fails
     */
    List<ExpenseSplit> getSplitsForExpense(int expenseId) throws Exception;
    
    /**
     * Retrieves balances for all roommates.
     * 
     * @return list of balances
     * @throws Exception if database operation fails
     */
    List<Balance> getBalances() throws Exception;
    
    /**
     * Marks a member's dues as settled.
     * 
     * @param memberId the member ID
     * @return true if any rows were updated
     * @throws Exception if database operation fails
     */
    boolean settleMemberDues(int memberId) throws Exception;
    
    /**
     * Deletes an expense by ID.
     * 
     * @param expenseId the expense ID
     * @return true if expense was deleted
     * @throws Exception if database operation fails
     */
    boolean deleteExpense(int expenseId) throws Exception;
    
    /**
     * Searches expenses by description using PreparedStatement.
     * 
     * @param description the description to search for (partial match, case-insensitive)
     * @return list of matching expenses
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesByDescription(String description) throws Exception;
    
    /**
     * Searches expenses by payer name using PreparedStatement.
     * 
     * @param payerName the payer name to search for (partial match, case-insensitive)
     * @return list of matching expenses
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesByPayer(String payerName) throws Exception;
    
    /**
     * Searches expenses by exact expense ID using PreparedStatement.
     * 
     * @param expenseId the expense ID to search for
     * @return list of matching expenses (empty or single element)
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesById(int expenseId) throws Exception;
    
    /**
     * Searches expenses by exact date using PreparedStatement.
     * 
     * @param date the date to search for
     * @return list of matching expenses
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesByDate(java.time.LocalDate date) throws Exception;
    
    /**
     * Searches expenses by exact amount using PreparedStatement.
     * 
     * @param amount the amount to search for
     * @return list of matching expenses
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesByAmount(java.math.BigDecimal amount) throws Exception;
    
    /**
     * Searches expenses by date range using PreparedStatement.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of matching expenses
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) throws Exception;
    
    /**
     * Searches expenses by amount range using PreparedStatement.
     * 
     * @param minAmount the minimum amount (inclusive)
     * @param maxAmount the maximum amount (inclusive)
     * @return list of matching expenses
     * @throws Exception if database operation fails
     */
    List<Expense> searchExpensesByAmountRange(java.math.BigDecimal minAmount, java.math.BigDecimal maxAmount) throws Exception;
}