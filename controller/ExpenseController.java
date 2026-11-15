package controller;

import dao.DatabaseManager;
import dao.ExpenseDAO;
import dao.RoommateDAO;
import dao.UserDAO;
import interfaces.ExpenseRepository;
import interfaces.RoommateRepository;
import model.Balance;
import model.Expense;
import model.ExpenseSplit;
import model.Roommate;
import model.User;
import utils.ValidationUtils;
import exceptions.InvalidExpenseException;
import exceptions.InvalidRoommateException;
import exceptions.DuplicateUsernameException;
import exceptions.InsufficientParticipantException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Expense Controller - Business logic layer.
 * 
 * This class demonstrates:
 * - Interface-based dependency (ExpenseRepository, RoommateRepository)
 * - Custom exception handling (checked exceptions)
 * - Validation with custom exceptions
 * - Business logic separation
 * - try-catch-finally blocks
 * - throw and throws keywords
 * - Multiple catch blocks
 * - Exception wrapping
 * 
 * Syllabus Topics:
 * - Interface-based programming
 * - Checked exceptions
 * - Custom exceptions
 * - try-catch-finally
 * - Multiple catch blocks
 * - throw / throws
 * - Exception handling
 * - Exception chaining
 * - Validation
 * 
 * @see interfaces.ExpenseRepository
 * @see interfaces.RoommateRepository
 * @see exceptions.InvalidExpenseException
 * @see exceptions.InvalidRoommateException
 * @see exceptions.DuplicateUsernameException
 * @see exceptions.InsufficientParticipantException
 * @see utils.ValidationUtils
 */
public class ExpenseController {
    private ExpenseRepository expenseRepository;
    private RoommateRepository roommateRepository;
    private UserDAO userDAO;
    private int currentUserId;

    public ExpenseController() {
        // Using interface types for polymorphism
        this.expenseRepository = new ExpenseDAO();
        this.roommateRepository = new RoommateDAO();
        this.userDAO = new UserDAO();
        this.currentUserId = -1;
    }

    public User registerUser(String username, String password, String email) throws Exception {
        try {
            // Validate with custom exceptions
            ValidationUtils.validateUsername(username, usernameStr -> {
                try {
                    return userDAO.usernameExists(usernameStr);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            ValidationUtils.validatePassword(password);
            ValidationUtils.validateEmail(email);
            
            return userDAO.registerUser(username.trim(), password, 
                email != null ? email.trim() : "");
        } catch (DuplicateUsernameException | InvalidRoommateException e) {
            // Re-throw checked exceptions
            throw e;
        } catch (Exception e) {
            // Wrap unexpected exceptions
            throw new Exception("Registration failed: " + e.getMessage(), e);
        }
    }

    public User loginUser(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidRoommateException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidRoommateException("Password cannot be empty");
        }
        
        User user = userDAO.loginUser(username.trim(), password);
        if (user != null) {
            this.currentUserId = user.getUserId();
        }
        return user;
    }

    public void logout() {
        this.currentUserId = -1;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public boolean isLoggedIn() {
        return currentUserId != -1;
    }

    public List<Roommate> getAllRoommates() throws Exception {
        return roommateRepository.getAllRoommates();
    }

    public model.Roommate getRoommateById(int memberId) throws Exception {
        return roommateRepository.getRoommateById(memberId);
    }

    public int addRoommate(model.Roommate roommate) throws Exception {
        // Validate with custom exceptions
        ValidationUtils.validateRoommateName(roommate.getName());
        ValidationUtils.validatePhone(roommate.getPhone());
        ValidationUtils.validateUpiId(roommate.getUpiId());
        
        return roommateRepository.addRoommate(roommate);
    }

    public boolean updateRoommate(model.Roommate roommate) throws Exception {
        ValidationUtils.validateRoommateName(roommate.getName());
        ValidationUtils.validatePhone(roommate.getPhone());
        ValidationUtils.validateUpiId(roommate.getUpiId());
        
        return roommateRepository.updateRoommate(roommate);
    }

    public boolean deleteRoommate(int memberId) throws Exception {
        return roommateRepository.deleteRoommate(memberId);
    }

    public int addExpense(String description, BigDecimal totalAmount, int payerId,
                          List<Integer> participantIds, LocalDate expenseDate) throws Exception {
        
        try {
            // Validate with custom checked exceptions
            ValidationUtils.validateDescription(description);
            ValidationUtils.validateExpenseAmount(totalAmount);
            ValidationUtils.validateParticipants(participantIds);
            ValidationUtils.validateExpenseDate(expenseDate);
            
            // Validate payer exists
            model.Roommate payer = roommateRepository.getRoommateById(payerId);
            if (payer == null) {
                throw new InvalidExpenseException("Selected payer does not exist");
            }
            
            // Validate all participants exist
            for (Integer participantId : participantIds) {
                if (roommateRepository.getRoommateById(participantId) == null) {
                    throw new InvalidExpenseException("Participant with ID " + participantId + " does not exist");
                }
            }

            Expense expense = new Expense();
            expense.setDescription(description.trim());
            expense.setTotalAmount(totalAmount);
            expense.setPayerId(payerId);
            expense.setExpenseDate(expenseDate);

            BigDecimal shareAmount = totalAmount.divide(
                new BigDecimal(participantIds.size()), 2, BigDecimal.ROUND_HALF_UP);

            List<ExpenseSplit> splits = new ArrayList<>();
            for (Integer memberId : participantIds) {
                ExpenseSplit split = new ExpenseSplit(expense.getExpenseId(), memberId, shareAmount);
                splits.add(split);
            }
            expense.setSplits(splits);

            return expenseRepository.addExpense(expense);
            
        } catch (InvalidExpenseException | InsufficientParticipantException e) {
            // Re-throw checked exceptions
            throw e;
        } catch (Exception e) {
            // Wrap unexpected exceptions
            throw new InvalidExpenseException("Failed to add expense: " + e.getMessage(), e);
        }
    }

    public List<Expense> getAllExpenses() throws Exception {
        return expenseRepository.getAllExpenses();
    }

    public List<ExpenseSplit> getSplitsForExpense(int expenseId) throws Exception {
        return expenseRepository.getSplitsForExpense(expenseId);
    }

    public List<Balance> getBalances() throws Exception {
        return expenseRepository.getBalances();
    }

    public boolean settleMemberDues(int memberId) throws Exception {
        return expenseRepository.settleMemberDues(memberId);
    }

    public boolean deleteExpense(int expenseId) throws Exception {
        return expenseRepository.deleteExpense(expenseId);
    }

    /**
     * Smart search across all expense fields.
     *
     * Strategy (in order):
     *  1. If the query is a pure integer → search by Expense ID.
     *  2. If the query matches a date pattern (yyyy-MM-dd or dd/MM/yyyy
     *     or dd MMM yyyy) → search by exact date.
     *  3. If the query starts with "₹" or is a valid decimal number →
     *     search by exact amount.
     *  4. Otherwise → search by description first; if 0 results,
     *     search by payer name; merge both result sets (deduplicated).
     *
     * All paths delegate to PreparedStatement-backed DAO methods.
     * No data is modified (SELECT only).
     *
     * @param query  raw text from the search field
     * @return       list of matching Expense objects, never null
     * @throws Exception on database error
     */
    public List<Expense> searchExpenses(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            return expenseRepository.getAllExpenses();
        }

        String trimmed = query.trim();

        // 1. Pure integer → search by ID
        try {
            int id = Integer.parseInt(trimmed);
            return expenseRepository.searchExpensesById(id);
        } catch (NumberFormatException ignored) {}

        // 2. Date patterns: yyyy-MM-dd  |  dd/MM/yyyy  |  dd MMM yyyy
        java.time.LocalDate parsedDate = tryParseDate(trimmed);
        if (parsedDate != null) {
            return expenseRepository.searchExpensesByDate(parsedDate);
        }

        // 3. Amount: strip leading ₹ symbol and try BigDecimal parse
        String amountCandidate = trimmed.startsWith("₹") ? trimmed.substring(1).trim() : trimmed;
        try {
            java.math.BigDecimal amount = new java.math.BigDecimal(amountCandidate);
            List<Expense> byAmount = expenseRepository.searchExpensesByAmount(amount);
            if (!byAmount.isEmpty()) {
                return byAmount;
            }
        } catch (NumberFormatException ignored) {}

        // 4. Text search: description + payer (deduplicated by expense_id)
        List<Expense> byDesc  = expenseRepository.searchExpensesByDescription(trimmed);
        List<Expense> byPayer = expenseRepository.searchExpensesByPayer(trimmed);

        // Merge, keeping insertion order and no duplicates
        java.util.LinkedHashMap<Integer, Expense> merged = new java.util.LinkedHashMap<>();
        for (Expense e : byDesc)  { merged.put(e.getExpenseId(), e); }
        for (Expense e : byPayer) { merged.putIfAbsent(e.getExpenseId(), e); }

        return new ArrayList<>(merged.values());
    }

    /**
     * Attempts to parse a date string in common formats.
     * Returns null if no format matches.
     */
    private java.time.LocalDate tryParseDate(String s) {
        String[] patterns = {"yyyy-MM-dd", "dd/MM/yyyy", "dd MMM yyyy", "MM/dd/yyyy"};
        for (String pattern : patterns) {
            try {
                return java.time.LocalDate.parse(s,
                        java.time.format.DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {}
        }
        return null;
    }

    public void shutdown() {
        DatabaseManager.closeConnection();
    }
}