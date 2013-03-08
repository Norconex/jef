package com.norconex.jef.error;

import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Event thrown by the framework to all registered <code>ErrorHandler</code>
 * instances.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
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
