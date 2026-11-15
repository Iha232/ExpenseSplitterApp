package exceptions;

/**
 * Custom checked exception for invalid expense operations.
 * 
 * This exception demonstrates the concept of checked exceptions in Java.
 * Checked exceptions must be either caught or declared in the method signature
 * using the 'throws' keyword, enforcing proper error handling at compile time.
 * 
 * Syllabus Topics Covered:
 * - Checked exceptions
 * - Exception hierarchy (extends Exception)
 * - Custom exception creation
 * - Proper error messaging
 */
public class InvalidExpenseException extends Exception {
    
    /**
     * Constructs an InvalidExpenseException with the specified detail message.
     * 
     * @param message the detail message explaining why the expense is invalid
     */
    public InvalidExpenseException(String message) {
        super(message);
    }
    
    /**
     * Constructs an InvalidExpenseException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the underlying cause of this exception
     */
    public InvalidExpenseException(String message, Throwable cause) {
        super(message, cause);
    }
}