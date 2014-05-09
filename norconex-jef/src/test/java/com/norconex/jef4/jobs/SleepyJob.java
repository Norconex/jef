/**
 *
 */
package com.norconex.jef4.jobs;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.JobStatusUpdater;
import com.norconex.jef4.suite.JobSuite;

/**
 * Sleeps for a give number of seconds, and report itself every given seconds.
 *
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class SleepyJob implements IJob {

    private final int sleepSeconds;

    private final int reportSeconds;

    /**
     * Constructor.
     */
    public SleepyJob(int sleepSeconds, int reportSeconds) {
        super();
        this.sleepSeconds = sleepSeconds;
        this.reportSeconds = reportSeconds;
    }

    @Override
    public String getName() {
        return "Sleepy Job " + sleepSeconds + "-" + reportSeconds;
    }

    public String getDescription() {
        return "Sleep " + sleepSeconds + " seconds and report every "
                + reportSeconds + " seconds.";
    }

//    /**
//     * @see com.norconex.jef.IJob#getProgressMinimum()
//     */
//    public long getProgressMinimum() {
//        return 0;
//    }
//
//    /**
//     * @see com.norconex.jef.IJob#getProgressMaximum()
//     */
//    public long getProgressMaximum() {
//        return sleepSeconds;
//    }
//

    
//    @Override
//    public void execute(JobStatusUpdater statusUpdater, JobSuiteOLD suite) {
//        Sleeper.sleepSeconds(sleepSeconds);
////        long elapsedSeconds = progress.getProgress();
////        System.out.println("START PROGRESS IS: " + elapsedSeconds);
////
////        while (elapsedSeconds < sleepSeconds) {
////            Sleeper.sleepSeconds(1);
////            elapsedSeconds++;
////            if (elapsedSeconds % reportSeconds == 0) {
//////                LOG.info("[" + getId() + "] Slept for "
//////                      + (elapsedTime / 1000) + " seconds.");
////
////                System.out.println("[" + getId() + "] Slept for "
////                        + elapsedSeconds + " seconds.");
////            }
////            statusUpdater.setProgress(elapsedSeconds);
////            statusUpdater.setNote("Slept for " + elapsedSeconds + " seconds.");
//////            progress.incrementProgress(1);
//////            progress.setNote(
//////                    "Slept for " + progress.getProgress() + " seconds.");
////        }
////        statusUpdater.setNote(
////                "Done sleeping for " + progress.getProgress() + " seconds.");
//    }

//    @Override
//    public void stop(IJobStatus executionStatus, JobSuiteOLD suite) {
//        
//    }

    @Override
    public void execute(JobStatusUpdater statusUpdater, JobSuite suite) {
      double elapsedSeconds = statusUpdater.getProgress();
      System.out.println("START PROGRESS IS: " + elapsedSeconds);

      while (elapsedSeconds < sleepSeconds) {
          Sleeper.sleepSeconds(1);
          elapsedSeconds++;
          if (elapsedSeconds % reportSeconds == 0) {
//              LOG.info("[" + getId() + "] Slept for "
//                    + (elapsedTime / 1000) + " seconds.");

              System.out.println("[" + getName() + "] Slept for "
                      + elapsedSeconds + " seconds.");
          }
          statusUpdater.setProgress(elapsedSeconds / sleepSeconds);
          statusUpdater.setNote("Slept for " + elapsedSeconds + " seconds.");
//          progress.incrementProgress(1);
//          progress.setNote(
//                  "Slept for " + progress.getProgress() + " seconds.");
      }
      statusUpdater.setNote(
              "Done sleeping for " + statusUpdater.getProgress() + " seconds.");
    }

    @Override
    public void stop(IJobStatus executionStatus, JobSuite suite) {
        // TODO Auto-generated method stub
        
    }

}
