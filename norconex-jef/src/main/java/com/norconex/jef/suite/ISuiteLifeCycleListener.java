package com.norconex.jef.suite;

/**
 * Listener for life-cycle activities on a job suite.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public interface ISuiteLifeCycleListener {

    /**
     * Invoked when a job suite is stopped.
     * @param suite job suite
     */
    void suiteStopped(JobSuite suite);
    /**
     * Invoked when a job suite is stopping.
     * @param suite job suite
     */
    void suiteStopping(JobSuite suite);
    /**
     * Invoked when a job suite is started.
     * @param suite job suite
     */
    void suiteStarted(JobSuite suite);
    /**
     * Invoked when a job suite is aborted.  This method offers little in
     * terms of error handling.  Refer to
     * {@link com.norconex.jef.error.IErrorHandler} to implement error handing.
     * @param suite job suite
     */
    void suiteAborted(JobSuite suite);
    /**
     * Invoked when a job suite finished executing.  A job may finish
     * without having completed successfully.
     * @param suite job suite
     * @since 2.0
     */
    void suiteTerminatedPrematuraly(JobSuite suite);
    /**
     * Invoked when a job suite completes.  A completed job suite is one
     * where all job executions returned <code>true</code> and
     * progress is at 100%.  Given that jobs are implemented correctly,
     * this is usually a good indication of success.
     * @param suite job suite
     */
    void suiteCompleted(JobSuite suite);
}
