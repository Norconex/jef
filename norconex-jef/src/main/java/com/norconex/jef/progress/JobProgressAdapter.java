package com.norconex.jef.progress;


/**
 * Adapter for a job life-cycle.  Default implementation for all
 * methods do nothing.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public class JobProgressAdapter implements IJobProgressListener {

    @Override
    public void jobTerminatedPrematuraly(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobProgressed(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobStarted(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobResumed(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobSkipped(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobStopped(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobStopping(final IJobStatus progress) {
        // do nothing
    }
    
    @Override
    public void jobRunningVerified(final IJobStatus progress) {
        // do nothing
    }
    @Override
    public void jobCompleted(IJobStatus progress) {
        // do nothing
    }
}
