package com.norconex.jef.exec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * <code>Rerunner</code> is responsible for executing <code>IRerunnable</code>
 * instances.  Upon reaching the maximum number of retries allowed, it 
 * will return the last exception encountered if there was one, or throw
 * a {@link RuntimeException} if {@link IRerunnable} simply returned
 * <code>false</code>. 
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public class Rerunner {

    /** Logger. */
    private static final Logger LOG = LogManager.getLogger(Rerunner.class);

    /** Default maximum number of retries. */
    public static final int DEFAULT_MAX_RERUN_ATTEMPTS = 10;
    /** Default wait time (milliseconds) between reruns. */
    public static final long DEFAULT_RERUN_SLEEP_TIME = 5 * 1000;
    
    /** Maximum number of re-run attempts. */
    private final int maxRerunAttempts;
    /** Time to wait between each run. */
    private final long sleepTime;
    /** Exception filter. */
    private final IExceptionFilter exceptionFilter;

    /**
     * Creates a new instance of <code>Rerunner</code> using the default
     * maximum re-run attempts and default re-run wait time.
     */
    public Rerunner() {
        this(DEFAULT_MAX_RERUN_ATTEMPTS);
    }
    /**
     * Creates a new instance of <code>Rerunner</code> using the default
     * re-run wait time.
     * @param maxRerunAttempts maximum number of execution attempts
     */
    public Rerunner(int maxRerunAttempts) {
        this(maxRerunAttempts, DEFAULT_RERUN_SLEEP_TIME);
    }
    /**
     * Creates a new instance of <code>Rerunner</code>.
     * @param maxRecoveryAttempts maximum number of execution attempts
     * @param sleepTime number of milliseconds to wait between each executions
     */
    public Rerunner(
            int maxRecoveryAttempts,
            long sleepTime) {
        this(null, maxRecoveryAttempts, sleepTime);
    }
    /**
     * Creates a new instance of <code>Rerunner</code> which will re-run code
     * triggering exceptions only if the given exception is accepted by
     * the {@link IExceptionFilter}. Uses the default
     * maximum re-run attempts and default re-run wait time.
     * @param exceptionFilter exception filter
     */    
    public Rerunner(
            IExceptionFilter exceptionFilter) {
        this(exceptionFilter, DEFAULT_MAX_RERUN_ATTEMPTS);
    }
    /**
     * Creates a new instance of <code>Rerunner</code> which will re-run code
     * triggering exceptions only if the given exception is accepted by
     * the {@link IExceptionFilter}. Uses the default re-run wait time.
     * @param exceptionFilter exception filter
     * @param maxRerunAttempts maximum number of execution attempts
     */
    public Rerunner(
            IExceptionFilter exceptionFilter,
            int maxRerunAttempts) {
        this(exceptionFilter, maxRerunAttempts, DEFAULT_RERUN_SLEEP_TIME);
    }
    /**
    /**
     * Creates a new instance of <code>Rerunner</code> which will re-run code
     * triggering exceptions only if the given exception is accepted by
     * the {@link IExceptionFilter}.
     * @param exceptionFilter exception filter
     * @param maxRerunAttempts maximum number of execution attempts
     * @param sleepTime number of milliseconds to wait between each executions
     */
    public Rerunner(
            IExceptionFilter exceptionFilter,
            int maxRerunAttempts,
            long sleepTime) {
        super();
        this.maxRerunAttempts = maxRerunAttempts;
        this.sleepTime = sleepTime;
        this.exceptionFilter = exceptionFilter;
    }

    /**
     * Runs the {@link IRerunnable} instance.
     * @param rerunnable the code to run
     * @throws Exception last {@link IRerunnable} exception or 
     *         {@link RuntimeException} if otherwise no exception
     */
    @SuppressWarnings("nls")
    public void run(IRerunnable rerunnable) throws Exception {
        int attemptCount = 0;
        Exception exception = null;
        while (attemptCount < maxRerunAttempts) {
            try {
                rerunnable.run();
                return; // no exception, simply return
            } catch (Exception e) {
                if (exceptionFilter == null || exceptionFilter.accept(e)) {
                    exception = e;
                } else {
                    LOG.error("Unrecoverable exception encountered.", e);
                    throw e;
                }
            }
            attemptCount++;
            if (attemptCount < maxRerunAttempts) {
                LOG.warn("Execution failed, attempting to run again ("
                        + attemptCount + " of " + maxRerunAttempts
                        + ").", exception);
                Thread.sleep(sleepTime);
            }
        }
        String err = "Execution failed, maximum number of recovery "
                + "attempts reached. Aborting.";
        if (exception == null) {
            exception = new RuntimeException(err);
        }
        LOG.error(err, exception);
        throw exception;
    }
}
