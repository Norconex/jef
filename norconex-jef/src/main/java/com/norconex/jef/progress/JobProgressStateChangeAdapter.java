package com.norconex.jef.progress;


/**
 * Adapter for a job life-cycle, focusing on progress state changes.
 * The methods <code>jobFinished, jobProgressed,
 * jobStarted, jobResumed</code> all invoke
 * <code>jobStateChanged</code> for convenience.
 * Override that method for common behaviour upon any type of state change.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public class JobProgressStateChangeAdapter extends JobProgressAdapter {

    @Override
    public final void jobTerminatedPrematuraly(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobProgressed(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobStarted(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobResumed(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public void jobStopped(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public void jobStopping(final IJobStatus progress) {
        jobStateChanged(progress);
    }
    @Override
    public void jobCompleted(IJobStatus progress) {
        jobStateChanged(progress);
    }
    
    /**
     * Invoked when any of the following are invoked 
     * (unless they are overridden):
     * <code>jobTerminatedPrematuraly, jobProgressed, jobStarted, jobResumed,
     * jobStopping, jobStopped, jobCompleted</code>.
     * @param progress progress that changed
     */
    public void jobStateChanged(final IJobStatus progress) {
        // do nothing
    }
}
