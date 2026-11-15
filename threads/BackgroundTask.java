package threads;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Base class for background tasks that update Swing components.
 * 
 * This class demonstrates:
 * - SwingWorker for background processing
 * - Thread-safe GUI updates
 * - Progress reporting
 * - Cancellation support
 * - Proper EDT (Event Dispatch Thread) usage
 * 
 * Syllabus Topics Covered:
 * - SwingWorker
 * - Background processing
 * - EDT (Event Dispatch Thread)
 * - Thread-safe GUI updates
 * - publish/process pattern
 * - doInBackground/done methods
 * - Cancellation
 * - Thread safety
 * 
 * @see javax.swing.SwingWorker
 * @see javax.swing.SwingUtilities
 * @see java.awt.EventQueue
 * 
 * @param <T> result type
 * @param <V> progress value type
 */
public abstract class BackgroundTask<T, V> extends SwingWorker<T, V> {
    
    private final String taskName;
    private volatile boolean cancelled = false;
    
    /**
     * Constructs a BackgroundTask.
     * 
     * @param taskName name for logging/debugging
     */
    protected BackgroundTask(String taskName) {
        this.taskName = taskName;
    }
    
    /**
     * Executes the background task.
     * 
     * This method runs on a background thread (NOT the EDT).
     * Do NOT update Swing components directly from this method.
     * Use publish()/process() for progress updates.
     * 
     * @return result of the background computation
     * @throws Exception if computation fails
     */
    @Override
    protected abstract T doInBackground() throws Exception;
    
    /**
     * Process progress updates on the EDT.
     * 
     * This method is called on the EDT when publish() is called.
     * Safe to update Swing components here.
     * 
     * @param chunks list of progress values
     */
    @Override
    protected void process(List<V> chunks) {
        // Override in subclasses to handle progress updates
        for (V chunk : chunks) {
            onProgress(chunk);
        }
    }
    
    /**
     * Called when the background task completes.
     * 
     * This method runs on the EDT. Safe to update Swing components.
     * 
     * @param result the result of doInBackground()
     */
    @Override
    protected void done() {
        try {
            T result = get(); // This will throw exceptions from doInBackground
            onSuccess(result);
        } catch (InterruptedException e) {
            // Task was cancelled
            onCancelled();
        } catch (ExecutionException e) {
            // Exception occurred in doInBackground
            Throwable cause = e.getCause();
            onError(cause != null ? cause : e);
        } catch (CancellationException e) {
            onCancelled();
        }
    }
    
    /**
     * Called when progress is published.
     * 
     * @param value the progress value
     */
    protected void onProgress(V value) {
        // Override in subclasses
    }
    
    /**
     * Called when task completes successfully.
     * 
     * @param result the result
     */
    protected abstract void onSuccess(T result);
    
    /**
     * Called when task is cancelled.
     */
    protected void onCancelled() {
        // Override if needed
    }
    
    /**
     * Called when an error occurs.
     * 
     * @param error the error
     */
    protected abstract void onError(Throwable error);
    
    /**
     * Checks if the task was cancelled.
     * 
     * @return true if cancelled
     */
    public boolean isTaskCancelled() {
        return cancelled || super.isCancelled();
    }
    
    /**
     * Publishes a progress value.
     * 
     * This method is called from doInBackground() to report progress.
     * The process() method will be called on the EDT with the published values.
     * 
     * @param value the progress value
     */
    protected void publishProgress(V value) {
        if (!isCancelled()) {
            publish(value);
        }
    }
    
    /**
     * Sleeps for a specified duration, checking for cancellation.
     * 
     * @param millis milliseconds to sleep
     * @return true if sleep completed, false if interrupted
     */
    protected boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return !isCancelled();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Runs a task on the EDT and waits for completion.
     * 
     * @param task the task to run
     * @throws InterruptedException if interrupted
     * @throws java.lang.reflect.InvocationTargetException if task throws exception
     */
    protected static void runOnEDT(Runnable task) throws InterruptedException, java.lang.reflect.InvocationTargetException {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeAndWait(task);
        }
    }
    
    /**
     * Runs a task on the EDT asynchronously.
     * 
     * @param task the task to run
     */
    protected static void runOnEDTAsync(Runnable task) {
        SwingUtilities.invokeLater(task);
    }
}