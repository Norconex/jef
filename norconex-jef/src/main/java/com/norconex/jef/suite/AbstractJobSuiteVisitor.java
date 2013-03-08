package com.norconex.jef.suite;

import com.norconex.jef.IJob;
import com.norconex.jef.progress.IJobStatus;

/**
 * Convenience base implementation of {@link IJobSuiteVisitor}.
 * All methods are empty (do nothing) and are meant for developers to pick
 * and chose the method to overwrite.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 1.1
 */
public abstract class AbstractJobSuiteVisitor implements IJobSuiteVisitor {

    @Override
    public void visitJob(IJob job) {
        // do nothing
    }

    @Override
    public void visitJobProgress(IJobStatus jobProgress) {
        // do nothing
    }

    @Override
    public void visitJobSuite(JobSuite jobSuite) {
        // do nothing
    }
}
