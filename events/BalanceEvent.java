package events;

import model.Balance;

/**
 * Event fired when a balance-related operation occurs.
 * 
 * This class demonstrates events for balance calculations and settlements.
 * 
 * Syllabus Topics Covered:
 * - Event classes
 * - Event objects carrying data
 * - Event types
 */
public class BalanceEvent extends ApplicationEvent {
    
    public enum BalanceAction {
        REFRESHED,
        SETTLED
    }
    
    private final BalanceAction action;
    private final int memberId; // 0 means all members
    
    /**
     * Constructs a BalanceEvent.
     * 
     * @param source the event source
     * @param action the action that occurred
     * @param memberId the member ID affected (0 for all)
     */
    public BalanceEvent(Object source, BalanceAction action, int memberId) {
        super(source, "BalanceEvent");
        this.action = action;
        this.memberId = memberId;
    }
    
    /**
     * Returns the action that triggered this event.
     * 
     * @return the balance action
     */
    public BalanceAction getAction() {
        return action;
    }
    
    /**
     * Returns the member ID affected by this event.
     * 
     * @return the member ID (0 for all members)
     */
    public int getMemberId() {
        return memberId;
    }
    
    @Override
    public String getEventType() {
        return "BalanceEvent:" + action.name();
    }
    
    @Override
    public String toString() {
        return "BalanceEvent{" +
                "action=" + action +
                ", memberId=" + memberId +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}