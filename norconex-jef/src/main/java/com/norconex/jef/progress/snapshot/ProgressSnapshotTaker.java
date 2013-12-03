/* Copyright 2010-2013 Norconex Inc.
 *
 * This file is part of Norconex JEF.
 *
 * Norconex JEF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Norconex JEF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Norconex JEF. If not, see <http://www.gnu.org/licenses/>.
 */
package com.norconex.jef.progress.snapshot;


import com.norconex.jef.IJob;
import com.norconex.jef.IJobGroup;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.suite.JobSuite;

public final class ProgressSnapshotTaker {

    private ProgressSnapshotTaker() {
        super();
    }

    public static IProgressSnapshot takeSnapshot(JobSuite jobSuite) {
        return takeSnapshot(jobSuite, jobSuite.getRootJob());
    }

    private static IProgressSnapshot takeSnapshot(JobSuite jobSuite, IJob job) {
        IJobStatus progress = jobSuite.getJobProgress(job);
        ProgressSnapshot snapshot = new ProgressSnapshot();
        snapshot.setJobId(job.getId());
        if (progress == null) {
            return snapshot;
        }
        snapshot.setCompletionRatio(progress.getCompletionRatio());
        snapshot.setElapsedTime(progress.getElapsedTime());
        snapshot.setEndTime(progress.getEndTime());
        snapshot.setJobContext(progress.getJobContext());
        snapshot.setLastActivity(progress.getLastActivity());
        snapshot.setMetadata(progress.getMetadata());
        snapshot.setNote(progress.getNote());
        snapshot.setProgress(progress.getProgress());
        snapshot.setRecovery(progress.isRecovery());
        snapshot.setStartTime(progress.getStartTime());
        snapshot.setStatus(progress.getStatus());
        snapshot.setStopRequested(progress.isStopRequested());
        if (job instanceof IJobGroup) {
            IJob[] jobs = ((IJobGroup) job).getJobs();
            if (jobs != null && jobs.length > 0) {
                for (IJob childJob : jobs) {
                    snapshot.getChildren().add(
                            takeSnapshot(jobSuite, childJob));
                }
            }
        }
        return snapshot;
    }
}
