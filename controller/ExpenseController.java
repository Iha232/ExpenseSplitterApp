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

    public void shutdown() {
        DatabaseManager.closeConnection();
    }
}