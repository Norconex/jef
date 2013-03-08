package com.norconex.jef.progress.snapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.norconex.jef.IJobContext;

/*default*/ class ProgressSnapshot implements IProgressSnapshot {
    private static final long serialVersionUID = -1602020031077532682L;

    /*default*/ String jobId;
    /*default*/ IJobContext jobContext;
    /*default*/ String note;
    /*default*/ long progress;
    /*default*/ Date endTime;
    /*default*/ Date startTime;
    /*default*/ Date lastActivity;
    /*default*/ String metadata;
    /*default*/ boolean stopRequested;
    /*default*/ long elapsedTime;
    /*default*/ double completionRatio;
    /*default*/ Status status;
    /*default*/ boolean recovery;
    /*default*/ final List<IProgressSnapshot> children = 
            new ArrayList<IProgressSnapshot>();
    public String getJobId() {
        return jobId;
    }
    public IJobContext getJobContext() {
        return jobContext;
    }
    public String getNote() {
        return note;
    }
    public long getProgress() {
        return progress;
    }
    public Date getEndTime() {
        return endTime;
    }
    public Date getStartTime() {
        return startTime;
    }
    public Date getLastActivity() {
        return lastActivity;
    }
    public String getMetadata() {
        return metadata;
    }
    public boolean isStopRequested() {
        return stopRequested;
    }
    public long getElapsedTime() {
        return elapsedTime;
    }
    public double getCompletionRatio() {
        return completionRatio;
    }
    public Status getStatus() {
        return status;
    }
    public boolean isRecovery() {
        return recovery;
    }
    public List<IProgressSnapshot> getChildren() {
        return children;
    }
}