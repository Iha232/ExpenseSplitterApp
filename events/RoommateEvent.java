package events;

import model.Roommate;

/**
 * Event fired when a roommate-related operation occurs.
 * 
 * This class demonstrates specific event types for roommate operations.
 * 
 * Syllabus Topics Covered:
 * - Event classes
 * - Event objects carrying data
 * - Event types
 */
public class RoommateEvent extends ApplicationEvent {
    
    public enum RoommateAction {
        ADDED,
        UPDATED,
        DELETED
    }
    
    private final model.Roommate roommate;
    private final RoommateAction action;
    
    /**
     * Constructs a RoommateEvent.
     * 
     * @param source the event source
     * @param roommate the roommate associated with the event
     * @param action the action that occurred
     */
    public RoommateEvent(Object source, model.Roommate roommate, RoommateAction action) {
        super(source, "RoommateEvent");
        this.roommate = roommate;
        this.action = action;
    }
    
    /**
     * Returns the roommate associated with this event.
     * 
     * @return the roommate
     */
    public model.Roommate getRoommate() {
        return roommate;
    }
    
    /**
     * Returns the action that triggered this event.
     * 
     * @return the roommate action
     */
    public RoommateAction getAction() {
        return action;
    }
    
    @Override
    public String getEventType() {
        return "RoommateEvent:" + action.name();
    }
    
    @Override
    public String toString() {
        return "RoommateEvent{" +
                "action=" + action +
                ", roommateId=" + (roommate != null ? roommate.getMemberId() : "null") +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}