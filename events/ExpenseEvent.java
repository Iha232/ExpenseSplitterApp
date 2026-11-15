package events;

import model.Expense;

/**
 * Event fired when an expense-related operation occurs.
 * 
 * This class demonstrates specific event types extending the base ApplicationEvent.
 * It carries the expense data associated with the event.
 * 
 * Syllabus Topics Covered:
 * - Event classes
 * - Event objects carrying data
 * - Event types
 * - Event data encapsulation
 */
public class ExpenseEvent extends ApplicationEvent {
    
    public enum ExpenseAction {
        ADDED,
        UPDATED,
        DELETED,
        SETTLED
    }
    
    private final Expense expense;
    private final ExpenseAction action;
    
    /**
     * Constructs an ExpenseEvent.
     * 
     * @param source the event source
     * @param expense the expense associated with the event
     * @param action the action that occurred
     */
    public ExpenseEvent(Object source, Expense expense, ExpenseAction action) {
        super(source, "ExpenseEvent");
        this.expense = expense;
        this.action = action;
    }
    
    /**
     * Returns the expense associated with this event.
     * 
     * @return the expense
     */
    public Expense getExpense() {
        return expense;
    }
    
    /**
     * Returns the action that triggered this event.
     * 
     * @return the expense action
     */
    public ExpenseAction getAction() {
        return action;
    }
    
    @Override
    public String getEventType() {
        return "ExpenseEvent:" + action.name();
    }
    
    @Override
    public String toString() {
        return "ExpenseEvent{" +
                "action=" + action +
                ", expenseId=" + (expense != null ? expense.getExpenseId() : "null") +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}