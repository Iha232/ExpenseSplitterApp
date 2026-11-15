package utils;

import exceptions.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class for validation operations.
 * 
 * This class demonstrates validation with custom exceptions.
 * 
 * Syllabus Topics Covered:
 * - Custom checked exceptions
 * - Exception throwing (throw)
 * - Exception declaration (throws)
 * - Validation logic
 * - Business rule enforcement
 * 
 * @see exceptions.InvalidExpenseException
 * @see exceptions.InvalidRoommateException
 * @see exceptions.DuplicateUsernameException
 * @see exceptions.InsufficientParticipantException
 */
public final class ValidationUtils {
    
    // Private constructor to prevent instantiation
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates expense amount.
     * 
     * Demonstrates: throw, throws, BigDecimal comparison
     * 
     * @param amount the amount to validate
     * @throws InvalidExpenseException if amount is invalid
     */
    public static void validateExpenseAmount(BigDecimal amount) throws InvalidExpenseException {
        if (amount == null) {
            throw new InvalidExpenseException("Expense amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidExpenseException("Expense amount must be greater than zero");
        }
        if (amount.scale() > 2) {
            throw new InvalidExpenseException("Expense amount cannot have more than 2 decimal places");
        }
        if (amount.compareTo(new BigDecimal("9999999.99")) > 0) {
            throw new InvalidExpenseException("Expense amount cannot exceed ₹99,99,999.99");
        }
    }
    
    /**
     * Validates expense description.
     * 
     * @param description the description to validate
     * @throws InvalidExpenseException if description is invalid
     */
    public static void validateDescription(String description) throws InvalidExpenseException {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidExpenseException("Expense description cannot be empty");
        }
        if (description.trim().length() > 255) {
            throw new InvalidExpenseException("Expense description cannot exceed 255 characters");
        }
    }
    
    /**
     * Validates expense participants.
     * 
     * @param participantIds list of participant IDs
     * @throws InsufficientParticipantException if insufficient participants
     */
    public static void validateParticipants(List<Integer> participantIds) throws InsufficientParticipantException {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new InsufficientParticipantException("At least one participant must be selected for expense splitting");
        }
        if (participantIds.size() == 1) {
            // Single participant is allowed but warn
        }
        // Check for duplicates
        if (participantIds.size() != new java.util.HashSet<>(participantIds).size()) {
            throw new InsufficientParticipantException("Duplicate participants are not allowed");
        }
    }
    
    /**
     * Validates roommate name.
     * 
     * @param name the name to validate
     * @throws InvalidRoommateException if name is invalid
     */
    public static void validateRoommateName(String name) throws InvalidRoommateException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRoommateException("Roommate name cannot be empty");
        }
        String trimmed = name.trim();
        if (trimmed.length() > 100) {
            throw new InvalidRoommateException("Roommate name cannot exceed 100 characters");
        }
        if (!trimmed.matches("[\\p{L}\\p{N} .'-]+")) {
            throw new InvalidRoommateException("Roommate name contains invalid characters");
        }
    }
    
    /**
     * Validates roommate phone number.
     * 
     * @param phone the phone number to validate
     * @throws InvalidRoommateException if phone is invalid
     */
    public static void validatePhone(String phone) throws InvalidRoommateException {
        if (phone != null && !phone.trim().isEmpty()) {
            String trimmed = phone.trim();
            if (!trimmed.matches("[0-9+\\-\\s()]{10,20}")) {
                throw new InvalidRoommateException("Invalid phone number format");
            }
        }
    }
    
    /**
     * Validates UPI ID format.
     * 
     * @param upiId the UPI ID to validate
     * @throws InvalidRoommateException if UPI ID is invalid
     */
    public static void validateUpiId(String upiId) throws InvalidRoommateException {
        if (upiId != null && !upiId.trim().isEmpty()) {
            String trimmed = upiId.trim();
            if (!trimmed.contains("@") || trimmed.indexOf("@") <= 0 
                    || trimmed.indexOf("@") >= trimmed.length() - 1) {
                throw new InvalidRoommateException("Invalid UPI ID format (expected format: user@bank)");
            }
        }
    }
    
    /**
     * Validates username.
     * 
     * @param username the username to validate
     * @param usernameExists function to check if username exists
     * @throws DuplicateUsernameException if username is taken
     * @throws InvalidRoommateException if username format is invalid
     */
    public static void validateUsername(String username, java.util.function.Function<String, Boolean> usernameExists) 
            throws DuplicateUsernameException, InvalidRoommateException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidRoommateException("Username cannot be empty");
        }
        String trimmed = username.trim();
        if (trimmed.length() < 3) {
            throw new InvalidRoommateException("Username must be at least 3 characters");
        }
        if (trimmed.length() > 50) {
            throw new InvalidRoommateException("Username cannot exceed 50 characters");
        }
        if (!trimmed.matches("[a-zA-Z0-9_]+")) {
            throw new InvalidRoommateException("Username can only contain letters, numbers, and underscores");
        }
        if (usernameExists != null && usernameExists.apply(trimmed)) {
            throw new DuplicateUsernameException("Username '" + trimmed + "' is already taken");
        }
    }
    
    /**
     * Validates password.
     * 
     * @param password the password to validate
     * @throws InvalidRoommateException if password is invalid
     */
    public static void validatePassword(String password) throws InvalidRoommateException {
        if (password == null || password.isEmpty()) {
            throw new InvalidRoommateException("Password cannot be empty");
        }
        if (password.length() < 4) {
            throw new InvalidRoommateException("Password must be at least 4 characters");
        }
        if (password.length() > 100) {
            throw new InvalidRoommateException("Password cannot exceed 100 characters");
        }
    }
    
    /**
     * Validates email format.
     * 
     * @param email the email to validate
     * @throws InvalidRoommateException if email format is invalid
     */
    public static void validateEmail(String email) throws InvalidRoommateException {
        if (email != null && !email.trim().isEmpty()) {
            String trimmed = email.trim();
            if (!trimmed.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                throw new InvalidRoommateException("Invalid email format");
            }
        }
    }
    
    /**
     * Validates expense date.
     * 
     * @param date the date to validate
     * @throws InvalidExpenseException if date is invalid
     */
    public static void validateExpenseDate(java.time.LocalDate date) throws InvalidExpenseException {
        if (date == null) {
            throw new InvalidExpenseException("Expense date cannot be null");
        }
        if (date.isAfter(java.time.LocalDate.now())) {
            throw new InvalidExpenseException("Expense date cannot be in the future");
        }
    }
}