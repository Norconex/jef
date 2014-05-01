/**
 * 
 */
package com.norconex.jef.jobs;

import com.norconex.jef.AbstractResumableJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuiteOLD;

/**
 * Sleeps for a give number of seconds, and report itself every given seconds.
 * 
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class RecoverableSleepyJob extends AbstractResumableJob {

    private final int sleepSeconds;

    private final int reportSeconds;

    /**
     * Constructor.
     */
    public RecoverableSleepyJob(int sleepSeconds, int reportSeconds) {
        super();
        this.sleepSeconds = sleepSeconds;
        this.reportSeconds = reportSeconds;
    }

    /**
     * @see com.norconex.jef.IJob#getId()
     */
    @Override
    public String getId() {
        return "job.sleep." + sleepSeconds + "-" + reportSeconds;
    }

    /**
     * @see com.norconex.jef.IJob#getDescription()
     */
    public String getDescription() {
        return "Sleep " + sleepSeconds + " seconds and report every "
                + reportSeconds + " seconds.";
    }

    /**
     * @see com.norconex.jef.IJob#getProgressMinimum()
     */
    public long getProgressMinimum() {
        return 0;
    }

    /**
     * @see com.norconex.jef.IJob#getProgressMaximum()
     */
    public long getProgressMaximum() {
        return sleepSeconds;
    }

    @Override
    protected void startExecution(JobProgress progress, JobSuiteOLD context) {
        sleep(progress);
    }

    @Override
    protected void resumeExecution(JobProgress progress, JobSuiteOLD context) {
        
        sleep(progress);
    }

    private void sleep(JobProgress progress) {
        long elapsedTime = progress.getProgress() * 1000;
        while (elapsedTime < sleepSeconds * 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new JobException(e);
            }
            elapsedTime += 1000;
            progress.incrementProgress(1);
            progress.setNote(
                    "Slept for " + progress.getProgress() + " seconds.");
        }
        progress.setNote(
                "Done sleeping for " + progress.getProgress() + " seconds.");
    }
    
	@Override
	public IJobContext createJobContext() {
		return new JobContext(
		        getDescription(), getProgressMinimum(), getProgressMaximum());
	}

	@Override
	public void stop(IJobStatus progress, JobSuiteOLD suite) {
		// Unstoppable
	}

}
