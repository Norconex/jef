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
        snapshot.completionRatio = progress.getCompletionRatio();
        snapshot.elapsedTime = progress.getElapsedTime();
        snapshot.endTime = progress.getEndTime();
        snapshot.jobContext = progress.getJobContext();
        snapshot.jobId = progress.getJobId();
        snapshot.lastActivity = progress.getLastActivity();
        snapshot.metadata = progress.getMetadata();
        snapshot.note = progress.getNote();
        snapshot.progress = progress.getProgress();
        snapshot.recovery = progress.isRecovery();
        snapshot.startTime = progress.getStartTime();
        snapshot.status = progress.getStatus();
        snapshot.stopRequested = progress.isStopRequested();
        if (job instanceof IJobGroup) {
            IJob[] jobs = ((IJobGroup) job).getJobs();
            if (jobs != null && jobs.length > 0) {
                for (IJob childJob : jobs) {
                    snapshot.children.add(takeSnapshot(jobSuite, childJob));
                }
            }
        }
        return snapshot;
    }
}
