package exceptions;

/**
 * Custom checked exception for database connection failures.
 * 
 * This exception wraps SQLException to provide a more meaningful checked exception
 * for database connectivity issues, demonstrating exception wrapping and chaining.
 * 
 * Syllabus Topics Covered:
 * - Checked exceptions
 * - Exception chaining (cause)
 * - Exception wrapping
 * - Resource handling
 */
public class DatabaseConnectionException extends Exception {
    
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}