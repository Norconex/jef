/* Copyright 2010-2018 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.jef5.job.impl;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.session.JobSession;
import com.norconex.jef5.session.JobSessionUpdater;
import com.norconex.jef5.suite.JobSuite;

/**
 * <p>
 * Sleeps for a give number of seconds, and report itself every given seconds.
 * </p>
 * <p>
 * This job is mainly useful for testing.
 * </p>
 * @author Pascal Essiembre
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
        return "Sleepy Job " + sleepSeconds + "-" + reportSeconds;
    }

    public String getDescription() {
        return "Sleep " + sleepSeconds + " seconds and report every "
                + reportSeconds + " seconds.";
    }

    @Override
    public void execute(JobSessionUpdater sessionUpdater, JobSuite suite) {
      double elapsedSeconds = sessionUpdater.getProgress();
      System.out.println("START PROGRESS IS: " + elapsedSeconds);

      while (elapsedSeconds < sleepSeconds) {
          Sleeper.sleepSeconds(1);
          elapsedSeconds++;
          if (elapsedSeconds % reportSeconds == 0) {
//              LOG.info("[" + getId() + "] Slept for "
//                    + (elapsedTime / 1000) + " seconds.");

              System.out.println("[" + getId() + "] Slept for "
                      + elapsedSeconds + " seconds.");
          }
          sessionUpdater.setProgress(elapsedSeconds / sleepSeconds);
          sessionUpdater.setNote("Slept for " + elapsedSeconds + " seconds.");
//          progress.incrementProgress(1);
//          progress.setNote(
//                  "Slept for " + progress.getProgress() + " seconds.");
      }
      sessionUpdater.setNote(
              "Done sleeping for " + sessionUpdater.getProgress() + " seconds.");
    }

    @Override
    public void stop(JobSession executionStatus, JobSuite suite) {
        // TODO stop sleeping
        
    }

}
