package exceptions;

/**
 * Custom checked exception for invalid roommate operations.
 * 
 * This exception demonstrates checked exception handling for roommate validation.
 * 
 * Syllabus Topics Covered:
 * - Checked exceptions
 * - Custom exception creation
 * - Proper error messaging
 */
public class InvalidRoommateException extends Exception {
    
    public InvalidRoommateException(String message) {
        super(message);
    }
    
    public InvalidRoommateException(String message, Throwable cause) {
        super(message, cause);
    }
}