package interfaces;

import java.io.IOException;

/**
 * Interface for objects that can be exported to a text format.
 * 
 * This interface demonstrates the concept of marker/behavioral interfaces
 * that define export capabilities for different objects.
 * 
 * Syllabus Topics Covered:
 * - Interface definition
 * - Behavioral contracts
 * - Polymorphism
 * - Exception handling in interfaces (throws IOException)
 */
public interface Exportable {
    
    /**
     * Exports the object's data to a text format using PrintWriter.
     * 
     * This method demonstrates the use of PrintWriter for formatted output,
     * StringBuffer for efficient string building, and proper resource handling.
     * 
     * Syllabus Topics Covered:
     * - PrintWriter for formatted output
     * - StringBuffer for mutable string building
     * - IOException handling
     * - Resource management
     * 
     * @param writer the PrintWriter to write to
     * @throws IOException if an I/O error occurs
     */
    void export(java.io.PrintWriter writer) throws IOException;
    
    /**
     * Returns a string representation suitable for export.
     * 
     * @return string representation of the object's data
     */
    String toExportString();
}