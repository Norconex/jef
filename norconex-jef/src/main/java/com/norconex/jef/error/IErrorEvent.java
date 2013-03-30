package com.norconex.jef.error;

import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Event thrown by the framework to all registered <code>ErrorHandler</code>
 * instances.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface IErrorEvent {

    /**
     * Gets the exception behind this error.
     * @return the exception
     */
    Throwable getException();

    /**
     * Gets the job suite from which this error occurred.
     * @return job suite
     */
    JobSuite getJobSuite();

    /**
     * Gets the job progress of the job from which the error got triggered.
     * @return job progress
     */
    JobProgress getProgress();
}
