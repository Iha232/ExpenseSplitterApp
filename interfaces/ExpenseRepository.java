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
}