package threads;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized task executor for background operations.
 * 
 * This class demonstrates:
 * - Thread pool management
 * - ExecutorService
 * - Thread creation and management
 * - Multiple threads
 * - Thread synchronization
 * - Safe thread cancellation
 * - Thread lifecycle management
 * 
 * Syllabus Topics Covered:
 * - Thread creation
 * - Multiple threads
 * - ExecutorService
 * - Thread pools
 * - Synchronization
 * - Thread interruption (Thread.interrupt())
 * - Cooperative cancellation
 * - Thread lifecycle management
 * - Atomic variables for thread-safe operations
 * 
 * IMPORTANT: Thread.suspend(), Thread.resume(), and Thread.stop() are DEPRECATED
 * and UNSAFE. This class demonstrates safe alternatives:
 * - Pause/resume flags with synchronization
 * - Interruption using Thread.interrupt()
 * - Cooperative cancellation
 * - Thread lifecycle management
 * 
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.atomic.AtomicBoolean
 * @see java.util.concurrent.atomic.AtomicInteger
 */
public class TaskExecutor {
    
    // Single instance for application-wide thread pool
    private static final TaskExecutor INSTANCE = new TaskExecutor();
    
    // Thread pool for background tasks
    private final ExecutorService executorService;
    
    // Scheduled executor for periodic tasks
    private final ScheduledExecutorService scheduledExecutor;
    
    // Track active tasks
    private final ConcurrentHashMap<String, Future<?>> activeTasks;
    
    // Task counter for unique IDs
    private final AtomicInteger taskCounter;
    
    // Shutdown flag
    private final AtomicBoolean shutdownFlag;
    
    private TaskExecutor() {
        // Fixed thread pool for background tasks (adjust based on CPU cores)
        int coreCount = Runtime.getRuntime().availableProcessors();
        this.executorService = new ThreadPoolExecutor(
            coreCount,
            coreCount * 2,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "ExpenseSplitter-Worker-" + threadNumber.getAndIncrement());
                    t.setDaemon(true); // Daemon threads don't prevent JVM shutdown
                    t.setPriority(Thread.NORM_PRIORITY - 1); // Slightly lower priority
                    return t;
                }
            },
            new ThreadPoolExecutor.AbortPolicy()
        );
        
        // Single-threaded scheduled executor for periodic tasks
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ExpenseSplitter-Scheduler");
            t.setDaemon(true);
            return t;
        });
        
        this.activeTasks = new ConcurrentHashMap<>();
        this.taskCounter = new AtomicInteger(0);
        this.shutdownFlag = new AtomicBoolean(false);
    }
    
    /**
     * Returns the singleton instance.
     * 
     * @return TaskExecutor instance
     */
    public static TaskExecutor getInstance() {
        return INSTANCE;
    }
    
    /**
     * Submits a task for background execution.
     * 
     * Demonstrates: Thread creation, ExecutorService.submit()
     * 
     * @param task the task to execute
     * @param taskName unique name for the task
     * @return Future representing the task
     */
    public <T> Future<T> submitTask(Callable<T> task, String taskName) {
        if (shutdownFlag.get()) {
            throw new IllegalStateException("TaskExecutor has been shutdown");
        }
        
        String taskId = taskName + "-" + taskCounter.incrementAndGet();
        
        Future<T> future = executorService.submit(() -> {
            // Set thread name for debugging
            Thread.currentThread().setName("ExpenseSplitter-" + taskId);
            try {
                return task.call();
            } finally {
                activeTasks.remove(taskId);
            }
        });
        
        activeTasks.put(taskId, future);
        return future;
    }
    
    /**
     * Submits a Runnable task for background execution.
     * 
     * @param task the task to execute
     * @param taskName unique name for the task
     * @return Future representing the task
     */
    public Future<?> submitTask(Runnable task, String taskName) {
        return submitTask(Executors.callable(task), taskName);
    }
    
    /**
     * Schedules a task to run after a delay.
     * 
     * Demonstrates: ScheduledExecutorService, delayed execution
     * 
     * @param task the task to execute
     * @param delay delay duration
     * @param unit time unit
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit unit) {
        if (shutdownFlag.get()) {
            throw new IllegalStateException("TaskExecutor has been shutdown");
        }
        
        return scheduledExecutor.schedule(task, delay, unit);
    }
    
    /**
     * Schedules a task to run periodically.
     * 
     * Demonstrates: ScheduledExecutorService, periodic execution
     * 
     * @param task the task to execute
     * @param initialDelay initial delay
     * @param period period between executions
     * @param unit time unit
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> schedulePeriodicTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        if (shutdownFlag.get()) {
            throw new IllegalStateException("TaskExecutor has been shutdown");
        }
        
        return scheduledExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }
    
    /**
     * Cancels a specific task.
     * 
     * Demonstrates: Thread.interrupt(), Future.cancel()
     * 
     * Safe cancellation using interruption (not deprecated Thread.stop())
     * 
     * @param taskId the task ID to cancel
     * @param mayInterruptIfRunning whether to interrupt running task
     * @return true if task was cancelled
     */
    public boolean cancelTask(String taskId, boolean mayInterruptIfRunning) {
        Future<?> future = activeTasks.get(taskId);
        if (future != null) {
            boolean cancelled = future.cancel(mayInterruptIfRunning);
            if (cancelled) {
                activeTasks.remove(taskId);
            }
            return cancelled;
        }
        return false;
    }
    
    /**
     * Cancels all active tasks.
     * 
     * @param mayInterruptIfRunning whether to interrupt running tasks
     * @return number of tasks cancelled
     */
    public int cancelAllTasks(boolean mayInterruptIfRunning) {
        int cancelled = 0;
        for (Map.Entry<String, Future<?>> entry : activeTasks.entrySet()) {
            if (entry.getValue().cancel(mayInterruptIfRunning)) {
                cancelled++;
            }
        }
        activeTasks.clear();
        return cancelled;
    }
    
    /**
     * Checks if a task is running.
     * 
     * @param taskId the task ID
     * @return true if task is running
     */
    public boolean isTaskRunning(String taskId) {
        Future<?> future = activeTasks.get(taskId);
        return future != null && !future.isDone();
    }
    
    /**
     * Gets the number of active tasks.
     * 
     * @return number of active tasks
     */
    public int getActiveTaskCount() {
        return (int) activeTasks.values().stream().filter(f -> !f.isDone()).count();
    }
    
    /**
     * Waits for a task to complete.
     * 
     * @param taskId the task ID
     * @param timeout timeout duration
     * @param unit time unit
     * @return true if completed within timeout
     * @throws InterruptedException if interrupted
     */
    public boolean awaitTask(String taskId, long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> future = activeTasks.get(taskId);
        if (future != null) {
            try {
                future.get(timeout, unit);
            } catch (ExecutionException e) {
                // Task threw an exception
                throw new RuntimeException("Task execution failed", e.getCause());
            } catch (java.util.concurrent.TimeoutException e) {
                // Timeout - task didn't complete in time
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Shuts down the executor gracefully.
     * 
     * Demonstrates: ExecutorService.shutdown(), awaitTermination()
     * 
     * @param timeout timeout for graceful shutdown
     * @param unit time unit
     * @return true if shutdown completed
     * @throws InterruptedException if interrupted
     */
    public boolean shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        shutdownFlag.set(true);
        
        // Disable new tasks
        executorService.shutdown();
        scheduledExecutor.shutdown();
        
        // Wait for existing tasks to complete
        boolean executorTerminated = executorService.awaitTermination(timeout, unit);
        boolean schedulerTerminated = scheduledExecutor.awaitTermination(timeout, unit);
        
        if (!executorTerminated || !schedulerTerminated) {
            // Force shutdown if timeout exceeded
            executorService.shutdownNow();
            scheduledExecutor.shutdownNow();
            
            // Wait a bit more for forced termination
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS);
        }
        
        activeTasks.clear();
        return executorTerminated && schedulerTerminated;
    }
    
    /**
     * Creates a cancellable background task with progress reporting.
     * 
     * Demonstrates: Cooperative cancellation, progress reporting, AtomicBoolean for pause/resume
     * 
     * @param task the task with progress callback
     * @param taskName task name
     * @return TaskHandle for controlling the task
     */
    public TaskHandle submitCancellableTask(ProgressTask task, String taskName) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        AtomicBoolean paused = new AtomicBoolean(false);
        Object pauseLock = new Object();
        
        Future<?> future = submitTask(() -> {
            try {
                task.run(new ProgressContext() {
                    @Override
                    public void reportProgress(int current, int total, String message) {
                        // Check for cancellation
                        if (cancelled.get()) {
                            throw new CancellationException("Task cancelled");
                        }
                        
                        // Handle pause/resume
                        while (paused.get() && !cancelled.get()) {
                            synchronized (pauseLock) {
                                try {
                                    pauseLock.wait(100);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new CancellationException("Task interrupted");
                                }
                            }
                        }
                        
                        if (cancelled.get()) {
                            throw new CancellationException("Task cancelled");
                        }
                        
                        task.onProgress(current, total, message);
                    }
                    
                    @Override
                    public boolean isCancelled() {
                        return cancelled.get();
                    }
                });
            } catch (CancellationException e) {
                // Task was cancelled - normal termination
                return;
            } catch (Exception e) {
                task.onError(e);
                throw new RuntimeException(e);
            }
        }, taskName);
        
        return new TaskHandle() {
            @Override
            public void cancel() {
                cancelled.set(true);
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
                future.cancel(true);
            }
            
            @Override
            public void pause() {
                paused.set(true);
            }
            
            @Override
            public void resume() {
                paused.set(false);
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
            }
            
            @Override
            public boolean isCancelled() {
                return cancelled.get();
            }
            
            @Override
            public boolean isPaused() {
                return paused.get();
            }
            
            @Override
            public boolean isDone() {
                return future.isDone();
            }
        };
    }
    
    /**
     * Returns the singleton instance.
     * 
     * @return TaskExecutor instance
     */
    public static TaskExecutor getExecutor() {
        return INSTANCE;
    }
    
    // Inner interfaces for progress handling
    
    /**
     * Interface for progress-reporting tasks.
     */
    public interface ProgressTask {
        void run(ProgressContext context) throws Exception;
        void onProgress(int current, int total, String message);
        void onError(Exception e);
    }
    
    /**
     * Context for progress reporting with cancellation support.
     */
    public interface ProgressContext {
        void reportProgress(int current, int total, String message);
        boolean isCancelled();
    }
    
    /**
     * Handle for controlling a background task.
     */
    public interface TaskHandle {
        void cancel();
        void pause();
        void resume();
        boolean isCancelled();
        boolean isPaused();
        boolean isDone();
    }
    
    /**
     * Exception for task cancellation.
     * 
     * Extends RuntimeException to be an unchecked exception, simplifying
     * cancellation handling in lambdas and threads.
     */
    public static class CancellationException extends RuntimeException {
        public CancellationException(String message) {
            super(message);
        }
    }
}