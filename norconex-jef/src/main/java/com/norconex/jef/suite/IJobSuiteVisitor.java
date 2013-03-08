package com.norconex.jef.suite;

import com.norconex.jef.IJob;
import com.norconex.jef.progress.IJobStatus;

/**
 * Allows one to "visit" a job suite and walk its job hierarchy with 
 * minimal effort.  A job suite is visited in this order: job suite,
 * and jobs, alternating between job and job progress for each job.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 1.1
 */
public interface IJobSuiteVisitor {
    /**
     * Visits a job suite.
     * @param jobSuite job suite visited
     */
    void visitJobSuite(JobSuite jobSuite);
    /**
     * Visits a job.
     * @param job job visited
     */
    void visitJob(IJob job);
    /**
     * Visits a job progress.
     * @param jobProgress job progress visited
     */
    void visitJobProgress(IJobStatus jobProgress);
}
