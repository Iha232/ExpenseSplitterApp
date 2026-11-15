package interfaces;

import java.util.EventObject;

/**
 * Interface for notification event listeners.
 * 
 * This interface demonstrates the Observer pattern and event-driven programming
 * in Java, following the Delegation Event Model.
 * 
 * Syllabus Topics Covered:
 * - Event handling
 * - Delegation Event Model
 * - Event listeners
 * - Event sources
 * - Event objects
 * - Callback mechanisms
 * 
 * @param <T> the type of event object
 * @see events.ApplicationEvent
 * @see events.EventManager
 */
public interface NotificationListener<T extends EventObject> {
    
    /**
     * Called when a notification event occurs.
     * 
     * This method demonstrates the event handling mechanism where the listener
     * receives an event object containing information about what happened.
     * 
     * Syllabus Topics Covered:
     * - Event handling method
     * - Event object (EventObject)
     * - Event source
     * - Event listener interface
     * 
     * @param event the event object containing details about the notification
     */
    void onNotification(T event);
}