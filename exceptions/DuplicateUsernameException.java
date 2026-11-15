package exceptions;

/**
 * Custom checked exception for duplicate username during registration.
 * 
 * This exception demonstrates checked exception handling for business logic violations.
 * 
 * Syllabus Topics Covered:
 * - Checked exceptions
 * - Custom exception creation
 * - Business logic validation
 */
public class DuplicateUsernameException extends Exception {
    
    public DuplicateUsernameException(String message) {
        super(message);
    }
    
    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}