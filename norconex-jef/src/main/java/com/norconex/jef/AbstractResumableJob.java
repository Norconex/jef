package com.norconex.jef;

import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * <p>Convenience class separating normal execution from recovery.  If the job
 * is starting clean, the <code>startExecution</code> method will get invoked.
 * Else, provided that there is already progress on a starting job
 * and the job is incomplete (i.e. failure), the <code>resumeExecution</code>
 * method will be invoked.</p>
 *
 * <p>Typical usage of this class might be when extra (or different) steps
 * need to be undertaken when resuming a job.  Otherwise, there may be no
 * benefits to using this class over a straight implementation of
 * <code>IJob</code>.</p>
 * 
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public abstract class AbstractResumableJob implements IJob {

    /**
     * Constructor.
     */
    public AbstractResumableJob() {
        super();
    }

    /**
     * @see com.norconex.jef.IJob#execute(
     *              com.norconex.jef.progress.JobProgress,
     *              com.norconex.jef.suite.JobSuite)
     */
    public final void execute(
            final JobProgress progress, final JobSuite suite) {
        
        if (!progress.isRecovery()) {
            startExecution(progress, suite);
        } else if (progress.getStatus() != IJobStatus.Status.COMPLETED) {
            resumeExecution(progress, suite);
        }
    }

    /**
     * Starts the execution of a job.
     * @param progress job progress
     * @param suite job suite
     */
    protected abstract void startExecution(
            JobProgress progress, JobSuite suite);
    /**
     * Resumes the execution of a job.
     * @param progress job progress
     * @param suite job suite
     */
    protected abstract void resumeExecution(
            JobProgress progress, JobSuite suite);
}
