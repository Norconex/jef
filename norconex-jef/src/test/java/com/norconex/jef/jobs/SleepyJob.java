/**
 *
 */
package com.norconex.jef.jobs;

import com.norconex.commons.lang.Sleeper;
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

        long elapsedSeconds = progress.getProgress();
        System.out.println("START PROGRESS IS: " + elapsedSeconds);

        while (elapsedSeconds < sleepSeconds) {
            Sleeper.sleepSeconds(1);
            elapsedSeconds++;
            if (elapsedSeconds % reportSeconds == 0) {
//                LOG.info("[" + getId() + "] Slept for "
//                      + (elapsedTime / 1000) + " seconds.");

                System.out.println("[" + getId() + "] Slept for "
                        + elapsedSeconds + " seconds.");
            }
            progress.setProgress(elapsedSeconds);
            progress.setNote("Slept for " + elapsedSeconds + " seconds.");
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
