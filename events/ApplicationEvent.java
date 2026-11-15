package events;

import java.util.Date;
import java.util.EventObject;

/**
 * Base class for application events.
 * 
 * This class demonstrates the EventObject class and the Delegation Event Model.
 * All custom events in the application extend this class.
 * 
 * Syllabus Topics Covered:
 * - EventObject class
 * - Event classes
 * - Event sources
 * - Event hierarchy
 * - Serialization of events
 * 
 * @see java.util.EventObject
 * @see java.io.Serializable
 */
public abstract class ApplicationEvent extends EventObject {
    
    private final Date timestamp;
    private final String sourceDescription;
    
    /**
     * Constructs an ApplicationEvent with the specified source.
     * 
     * @param source the object on which the event initially occurred
     * @param sourceDescription description of the event source
     */
    protected ApplicationEvent(Object source, String sourceDescription) {
        super(source);
        this.timestamp = new Date();
        this.sourceDescription = sourceDescription;
    }
    
    /**
     * Returns the timestamp when the event occurred.
     * 
     * @return the event timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns a description of the event source.
     * 
     * @return description of the event source
     */
    public String getSourceDescription() {
        return sourceDescription;
    }
    
    /**
     * Returns the event type as a string.
     * 
     * @return the event type
     */
    public abstract String getEventType();
}