package com.norconex.jef;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;


/**
 * Job responsible for running a group of jobs synchronously.  Jobs are
 * executed in the order specified by the <code>IJob[]</code> array.
 * The progress of this
 * group reflects the average progress of all its jobs.  This job group
 * is considered completed and will only return when all its jobs are
 * done executing.  An exception in one job will stop the other jobs
 * in the group from executing.  Such an exception will
 * result in this group to fail.
 *
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
@SuppressWarnings("nls")
public class SyncJobGroup extends AbstractJobGroup {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(SyncJobGroup.class);

    public SyncJobGroup(final String id, final IJob[] jobs) {
        super(id, jobs, "Synchronous job group with " + jobs.length + " jobs.");
    }
    public SyncJobGroup(
            final String id, final IJob[] jobs, String description) {
        super(id, jobs, description);
    }

    /**
     * @see com.norconex.jef.IJob#execute(JobProgress, JobSuite)
     */
    public final void execute(
            final JobProgress progress, final JobSuite suite) {

        registerGroupProgressMonitoring(progress, suite);

        IJob[] jobs = getJobs();
        String failedJob = null;
        for (int i = 0; i < jobs.length; i++) {
            JobRunner runner = new JobRunner();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Synchronous group " + getId()
                        + " about to run synchronous job: " + jobs[i].getId());
            }
            if (!runner.runJob(jobs[i], suite)) {
                LOG.error(jobs[i].getId() + " failed.");
                failedJob = jobs[i].getId();
                break;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(jobs[i].getId() + " succeeded.");
                }
            }
            JobRunner.setCurrentJobId(getId());
        }

        unregisterGroupProgressMonitoring(suite);

        if (failedJob != null) {
            throw new JobException(
                    failedJob + " failed in sync group " + getId());
        }
    }
}
