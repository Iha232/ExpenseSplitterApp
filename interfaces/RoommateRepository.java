package interfaces;

import model.Roommate;

import java.util.List;

/**
 * Interface for roommate repository operations.
 * 
 * This interface demonstrates the contract for roommate data access operations.
 * 
 * Syllabus Topics Covered:
 * - Interface definition
 * - Abstraction
 * - Contract-based programming
 * - Polymorphism through interface implementation
 * 
 * @see dao.RoommateDAO
 */
public interface RoommateRepository {
    
    /**
     * Retrieves all roommates.
     * 
     * @return list of all roommates
     * @throws Exception if database operation fails
     */
    List<Roommate> getAllRoommates() throws Exception;
    
    /**
     * Retrieves a roommate by ID.
     * 
     * @param memberId the member ID
     * @return the roommate or null if not found
     * @throws Exception if database operation fails
     */
    model.Roommate getRoommateById(int memberId) throws Exception;
    
    /**
     * Adds a new roommate.
     * 
     * @param roommate the roommate to add
     * @return the generated member ID
     * @throws Exception if database operation fails
     */
    int addRoommate(model.Roommate roommate) throws Exception;
    
    /**
     * Updates an existing roommate.
     * 
     * @param roommate the roommate to update
     * @return true if roommate was updated
     * @throws Exception if database operation fails
     */
    boolean updateRoommate(model.Roommate roommate) throws Exception;
    
    /**
     * Deletes a roommate by ID.
     * 
     * @param memberId the member ID
     * @return true if roommate was deleted
     * @throws Exception if database operation fails
     */
    boolean deleteRoommate(int memberId) throws Exception;
}