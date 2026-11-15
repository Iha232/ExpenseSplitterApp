package exceptions;

/**
 * Custom checked exception for insufficient participants in expense splitting.
 * 
 * This exception validates business rules for expense splitting operations.
 * 
 * Syllabus Topics Covered:
 * - Checked exceptions
 * - Business logic validation
 * - Custom exception creation
 */
public class InsufficientParticipantException extends Exception {
    
    public InsufficientParticipantException(String message) {
        super(message);
    }
    
    public InsufficientParticipantException(String message, Throwable cause) {
        super(message, cause);
    }
}