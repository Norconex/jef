/**
 * 
 */
package com.norconex.jef.jobs;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Sleeps for a give number of seconds, and report itself every given seconds.
 * 
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class SleepyJob implements IJob {

    private final int sleepSeconds;

    private final int reportSeconds;

    public SleepyJob(int sleepSeconds, int reportSeconds) {
        super();
        this.sleepSeconds = sleepSeconds;
        this.reportSeconds = reportSeconds;
    }

    @Override
    public String getId() {
        return "job.sleep." + sleepSeconds + "-" + reportSeconds;
    }

    public String getDescription() {
        return "Sleep " + sleepSeconds + " seconds and report every "
                + reportSeconds + " seconds.";
    }

    public long getProgressMinimum() {
        return 0;
    }

    public long getProgressMaximum() {
        return sleepSeconds;
    }

    @Override
    public void execute(JobProgress progress, JobSuite suite)
            throws JobException {

        long elapsedTime = progress.getProgress() * 1000;
//        LOG.info("START PROGRESS IS: " + elapsedTime);
        System.out.println("START PROGRESS IS: " + elapsedTime);
        while (elapsedTime < sleepSeconds * 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new JobException(e);
            }
            elapsedTime += 1000;
            if (elapsedTime % (reportSeconds * 1000) == 0) {
//                LOG.info("[" + getId() + "] Slept for "
//                      + (elapsedTime / 1000) + " seconds.");

                System.out.println("[" + getId() + "] Slept for "
                        + (elapsedTime / 1000) + " seconds.");
                progress.setProgress((elapsedTime / 1000));
                progress.setNote(
                        "Slept for " + progress.getProgress() + " seconds.");
            }
//            progress.incrementProgress(1);
//            progress.setNote(
//                    "Slept for " + progress.getProgress() + " seconds.");
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
	public void stop(IJobStatus progress, JobSuite suite) {
	    // Unstoppable
	}

}
