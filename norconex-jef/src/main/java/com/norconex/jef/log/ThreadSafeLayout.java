package com.norconex.jef.log;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.norconex.jef.JobRunner;

/**
 * Log layout decorator, prefixing any existing layout with the job
 * id associated with the current thread, separated with colon-space (": ").
 * If no jobs are associated with
 * a given log event, the prefix "[non-job]: " will get prepended.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @see FileLogManager
 */
public class ThreadSafeLayout extends Layout {

    /** Log4j log layout. */
    private final Layout layout;
    
    /**
     * Constructor.
     * @param layout decorated layout
     */
    public ThreadSafeLayout(final Layout layout) {
        super();
        this.layout = layout;
    }

    /**
     * @see org.apache.log4j.Layout#activateOptions()
     */
    public void activateOptions() {
        layout.activateOptions();
    }

    /**
     * Calls the decorated instance, prefixing with job identifier.
     * @see org.apache.log4j.Layout#format(org.apache.log4j.spi.LoggingEvent)
     */
    @SuppressWarnings("nls")
    public String format(LoggingEvent evt) {
        String jobId = JobRunner.getCurrentJobId();
        if (jobId == null) {
            return "[non-job]: " + layout.format(evt);
        }
        return jobId + ": " + layout.format(evt);
    }

    /**
     * @see org.apache.log4j.Layout#ignoresThrowable()
     */
    public boolean ignoresThrowable() {
        return layout.ignoresThrowable();
    }
}
