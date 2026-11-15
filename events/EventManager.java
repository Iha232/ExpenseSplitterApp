package events;

import interfaces.NotificationListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central event manager for the application.
 * 
 * This class demonstrates the Delegation Event Model implementation:
 * - Event sources register with the manager
 * - Listeners register for specific event types
 * - Events are fired to all registered listeners
 * - Thread-safe listener management
 * 
 * Syllabus Topics Covered:
 * - Delegation Event Model
 * - Event sources
 * - Event listeners
 * - Event registration
 * - Event firing
 * - Thread-safe collections (ConcurrentHashMap, CopyOnWriteArrayList)
 * - Observer pattern
 * - Event dispatching
 * - Thread safety in event handling
 * 
 * @see interfaces.NotificationListener
 * @see ApplicationEvent
 */
public class EventManager {
    
    // Map of event class to list of listeners
    private final Map<Class<? extends ApplicationEvent>, List<NotificationListener<? extends ApplicationEvent>>> listeners;
    
    // Global listeners that receive all events
    private final List<NotificationListener<ApplicationEvent>> globalListeners;
    
    private static final EventManager INSTANCE = new EventManager();
    
    /**
     * Private constructor for singleton pattern.
     * 
     * Syllabus Topics Covered:
     * - Singleton pattern
     * - Private constructor
     * - Static factory method
     */
    private EventManager() {
        this.listeners = new ConcurrentHashMap<>();
        this.globalListeners = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Returns the singleton instance of EventManager.
     * 
     * @return the EventManager instance
     */
    public static EventManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Registers a listener for a specific event type.
     * 
     * This method demonstrates event listener registration in the Delegation Event Model.
     * 
     * Syllabus Topics Covered:
     * - Event listener registration
     * - Event listener interfaces
     * - Generic event handling
     * - Thread-safe registration
     * 
     * @param eventType the type of event to listen for
     * @param listener the listener to register
     * @param <T> the event type
     */
    public <T extends ApplicationEvent> void addListener(Class<T> eventType, NotificationListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    /**
     * Removes a listener for a specific event type.
     * 
     * @param eventType the type of event
     * @param listener the listener to remove
     * @param <T> the event type
     */
    public <T extends ApplicationEvent> void removeListener(Class<T> eventType, NotificationListener<T> listener) {
        List<NotificationListener<? extends ApplicationEvent>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }
    
    /**
     * Adds a global listener that receives all events.
     * 
     * @param listener the global listener
     */
    public void addGlobalListener(NotificationListener<ApplicationEvent> listener) {
        globalListeners.add(listener);
    }
    
    /**
     * Removes a global listener.
     * 
     * @param listener the global listener to remove
     */
    public void removeGlobalListener(NotificationListener<ApplicationEvent> listener) {
        globalListeners.remove(listener);
    }
    
    /**
     * Fires an event to all registered listeners.
     * 
     * This method demonstrates event firing in the Delegation Event Model.
     * It notifies both specific listeners and global listeners.
     * 
     * Syllabus Topics Covered:
     * - Event firing
     * - Event dispatching
     * - Event propagation
     * - Listener notification
     * - Iterating over listeners
     * 
     * @param event the event to fire
     */
    public void fireEvent(ApplicationEvent event) {
        // Notify specific listeners for this event type
        Class<? extends ApplicationEvent> eventClass = event.getClass();
        List<NotificationListener<? extends ApplicationEvent>> specificListeners = listeners.get(eventClass);
        
        if (specificListeners != null) {
            for (NotificationListener<? extends ApplicationEvent> listener : specificListeners) {
                try {
                    // Safe cast since we know the event type matches
                    @SuppressWarnings("unchecked")
                    NotificationListener<ApplicationEvent> typedListener = 
                        (NotificationListener<ApplicationEvent>) listener;
                    typedListener.onNotification(event);
                } catch (Exception e) {
                    // Log error but continue notifying other listeners
                    System.err.println("Error notifying listener: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        // Notify global listeners
        for (NotificationListener<ApplicationEvent> listener : globalListeners) {
            try {
                listener.onNotification(event);
            } catch (Exception e) {
                System.err.println("Error notifying global listener: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Clears all listeners.
     * 
     * Useful for cleanup during application shutdown.
     */
    public void clearAllListeners() {
        listeners.clear();
        globalListeners.clear();
    }
    
    /**
     * Returns the number of listeners for a specific event type.
     * 
     * @param eventType the event type
     * @return number of listeners
     */
    public int getListenerCount(Class<? extends ApplicationEvent> eventType) {
        List<NotificationListener<? extends ApplicationEvent>> list = listeners.get(eventType);
        return list != null ? list.size() : 0;
    }
    
    /**
     * Returns the number of global listeners.
     * 
     * @return number of global listeners
     */
    public int getGlobalListenerCount() {
        return globalListeners.size();
    }
}