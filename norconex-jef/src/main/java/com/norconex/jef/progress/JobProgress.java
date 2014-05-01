/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef.progress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef.IJobContext;
import com.norconex.jef.suite.ISuiteStopRequestListener;

/**
 * Responsible for keeping track reporting job execution progress.
 * @author Pascal Essiembre
 */
public final class JobProgress 
        implements IJobStatus, ISuiteStopRequestListener  {

    private static final long serialVersionUID = -4753448803091568593L;

    /** Frequency of activity check. */
    public static final long ACTIVITY_CHECK = 5 * 1000;

    /** Activity timeout. */
    private static final long ACTIVITY_TIMEOUT = 10 * 1000;

    /** Logger. */
    private static final Logger LOG =
        LogManager.getLogger(JobProgress.class);

    /** ID of the job associated with this progress. */
    private final String jobId;
    /** Time-related information about job execution. */
    private final JobElapsedTime elapsedTime;
    /** Current progress. */
    private long progress;
    /** Note associated with current progress. */
    private String note;
    /** Data used to facilitate job recovery. */
    private String metadata;
    /** Whether we are recovering from a failed job. */
    private final boolean recovery;
    /** The last failed job progress. */
    private final IJobStatus lastFailedJobStatus;

    private final IJobContext jobContext;
    
    private boolean stopRequested;
    
    /** Progress change listeners. */
    private final List<IJobProgressListener> listeners =
            Collections.synchronizedList(new ArrayList<IJobProgressListener>());

    /**
     * Constructor.
     * @param jobId job id
     * @param jobContext job contextual info
     * @param elapsedTime execution elapsed time
     */
    public JobProgress(
            String jobId,
            IJobContext jobContext,
            final JobElapsedTime elapsedTime) {
        super();
        this.jobId = jobId;
        this.jobContext = jobContext;
        this.elapsedTime = elapsedTime;
        this.recovery = false;
        this.lastFailedJobStatus = null;
    }

    /**
     * Constructor.
     * @param jobId job id
     * @param jobContext job contextual info
     * @param elapsedTime execution elapsed time
     * @param progress current job progress
     * @since 1.1
     */
    public JobProgress(
            String jobId,
            IJobContext jobContext,
            final JobElapsedTime elapsedTime,
            final long progress) {
        super();
        this.jobId = jobId;
        this.jobContext = jobContext;
        this.elapsedTime = elapsedTime;
        this.progress = progress;
        this.recovery = false;
        this.lastFailedJobStatus = null;
    }
    
    
    /**
     * Creates a job progress initialised with values from a failed job progress
     * we are recovering from.
     * @param jobId job id
     * @param jobContext job contextual info
     * @param jobStatus job status
     * @param elapsedTime execution elapsed time
     * @since 1.1.1
     */
    public JobProgress(
            String jobId,
            IJobContext jobContext,
            final IJobStatus jobStatus, 
            final JobElapsedTime elapsedTime) {
        super();
        this.jobId = jobId;
        this.jobContext = jobContext;
        this.lastFailedJobStatus = jobStatus;
        this.elapsedTime = elapsedTime;
        this.elapsedTime.setStartTime(jobStatus.getStartTime());
        this.elapsedTime.setEndTime(jobStatus.getEndTime());
        this.progress = jobStatus.getProgress();
        this.note = jobStatus.getNote();
        this.metadata = jobStatus.getMetadata();
        this.recovery = true;
    }

    /**
     * Gets contextual information about a job.
     * @return job context
     * @since 2.0
     */
    @Override
    public IJobContext getJobContext() {
        return jobContext;
    }

    /**
     * Gets the current progress note.
     * @return Returns the note.
     */
    @Override
    public String getNote() {
        return note;
    }

    /**
     * Sets teh current progress note.
     * @param note The note to set.
     */
    public synchronized void setNote(final String note) {
        this.note = note;
        fireProgressChanged();
    }

    /**
     * Gets the current progress.
     * @return Returns the progress.
     */
    @Override
    public long getProgress() {
        if (progress < jobContext.getProgressMinimum()) {
            return jobContext.getProgressMinimum();
        }
        return progress;
    }

    /**
     * Sets the current progress.
     * @param progress The progress to set.
     */
    public synchronized void setProgress(final long progress) {
        this.progress = progress;
        fireProgressChanged();
    }

    /**
     * Increments the current progress by the given increment value.
     * @param incrementValue value by which the progress will be incremented
     */
    public synchronized void incrementProgress(final long incrementValue) {
        this.progress += incrementValue;
        fireProgressChanged();
    }

    /**
     * Gets the associated job unique identifier.
     * @return the associated job unique identifier
     */
    @Override
    public String getJobId() {
        return jobId;
    }

    /**
     * Gets the end date of this progress.  <code>null</code> if the progress
     * never finished.
     * @return end time
     */
    @Override
    public Date getEndTime() {
        if (elapsedTime.getEndTime() != null) {
            return (Date) elapsedTime.getEndTime().clone();
        }
        return null;
    }

    /**
     * Checks whether the job execution represented by this progress is still
     * running.
     * @return <code>true</code> if still running
     */
    private boolean isRunning() {
        Date date = elapsedTime.getLastActivity();
        if (date == null) {
            return false;
        }
        if (isPrematurlyEnded()) {
        	return false;
        }
        return (System.currentTimeMillis() - date.getTime())
                < ACTIVITY_TIMEOUT;
    }

    /**
     * Gets the date on which last activity on the job execution occured.
     * @return last activity date
     */
    @Override
    public Date getLastActivity() {
        if (elapsedTime.getLastActivity() != null) {
            return (Date) elapsedTime.getLastActivity().clone();
        }
        return null;
    }

    /**
     * Gets meta-data associated with this job progress.
     * @return meta-data
     */
    @Override
    public String getMetadata() {
        return metadata;
    }

    /**
     * Sets meta-data associated with this job progress.
     * @param metadata meta-data
     */
    public synchronized void setMetadata(final String metadata) {
        this.metadata = metadata;
        fireProgressChanged();
    }

    /**
     * Gets the end start of this progress.  <code>null</code> if the progress
     * never started.
     * @return start time
     */
    @Override
    public Date getStartTime() {
        if (elapsedTime.getStartTime() != null) {
            return (Date) elapsedTime.getStartTime().clone();
        }
        return null;
    }

    /**
     * Gets how long it took to finish a job, in milliseconds.
     * @return job execution elapsed time
     */
    @Override
    public long getElapsedTime() {
        return elapsedTime.getElapsedTime();
    }
    /**
     * Gets a string representation of how long it tooks to finish a job.
     * @return job execution elapsed time, as a string
     */
    public String getElapsedTimeAsString() {
        return elapsedTime.getElapsedTimeAsString();
    }

    /**
     * Adds a job progress listener.
     * @param listener job progress listener to add
     */
    public void addJobProgressListener(final IJobProgressListener listener) {
        synchronized (listeners) {
            listeners.add(0, listener);
        }
    }
    /**
     * Removes a job progress listener.
     * @param listener job progress listener to remove
     */
    public void removeJobProgressListener(final IJobProgressListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    /**
     * Gets the job completion ration.
     * @return a double between 0 and 1
     */
    @Override
    public double getCompletionRatio() {
        long total = jobContext.getProgressMaximum() 
                - jobContext.getProgressMinimum();
        return (double) progress / (double) total;
    }

    /**
     * Gets the job execution status.
     * @return execution status
     */
    @Override
    public Status getStatus() {
        // The order is important to establish status
        if (isAborted())   { return Status.ABORTED;   }
        if (isStopped())   { return Status.STOPPED;   }
        if (isStopping())   { return Status.STOPPING;   }
        if (isCompleted()) { return Status.COMPLETED; }
        if (isPrematurlyEnded())  { return Status.PREMATURE_TERMINATION;  }
        if (isRunning())   { return Status.RUNNING;   }
        return Status.UNKNOWN;
    }

    
    private boolean isStopped() {
        return stopRequested && !isRunning();
    }
    
    private boolean isStopping() {
        return stopRequested && isRunning();
    }

    @Override
    public boolean isStopRequested() {
        return stopRequested;
    }

    /**
     * Whether this progress is from a recovery attempt from a previously 
     * failed job.
     * @return <code>true</code> if the current progress is a recovery
     * @since 1.1.1
     */
    @Override
    public boolean isRecovery() {
        return this.recovery;
    }

    /**
     * Gets the last previously failed job progress, if any (assuming
     * we are in recovery).
     * @return last failed job progress
     * @since 1.1.1
     */
    public IJobStatus getLastFailedJobStatus() {
        return lastFailedJobStatus;
    }

    /**
     * Checks whether the current progress status matches any of the supplied
     * statuses.
     * @param status one or more statuses to match
     * @return <code>true</code> if progress status matches supplied statuses
     * @since 2.0
     */
    public boolean isStatus(IJobStatus.Status... status) {
        Status thisStatus = getStatus();
        for (Status astatus : status) {
            if (thisStatus == astatus) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Notifies all listeners of a change of progress.
     */
    @SuppressWarnings("nls")
    protected synchronized void fireProgressChanged() {
        if (progress > jobContext.getProgressMaximum()) {
            LOG.warn("Execution progress ("
                    + progress + ") exceeds allowed job "
                    + "maximum (" + jobContext.getProgressMaximum()
                    + ") for job: " + getJobId());
        }
        synchronized (listeners) {
            for (IJobProgressListener listener : listeners) {
                listener.jobProgressed(this);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Progress changed: job='" + getJobId()
                    + "'; progress=" + getProgress()
                    + "; note='" + getNote() + "'.");
        }
    }

    /**
     * Checks whether the job was started or not.  This is not an indication
     * that a job is currently running.
     * @return <code>true</code> if job was started
     */
    private boolean isStarted() {
        return elapsedTime.getStartTime() != null;
    }
    /**
     * Checks whether the job has finished.  This is not an indication
     * that a job was completed or that there were no errors.
     * @return <code>true</code> if job finished
     */
    private boolean isPrematurlyEnded() {
        return elapsedTime.getEndTime() != null;
    }
    /**
     * Checks whether the job execution has completed.
     * @return <code>true</code> if the job execution is complete
     */
    private boolean isCompleted() {
        return Math.floor(getCompletionRatio()) >= 1;
    }
    /**
     * Checks whether the job was aborted or not (i.e. killed).  That is, if
     * the job started and is no longer running, while it never never
     * marked as finished.  Remember that under normal conditions, a job
     * should always finish, whether it failed or not.  An aborted progress
     * is usually the results of a job suite which got "killed" in the middle
     * of its execution (not having the chance to return properly).
     * @return <code>true</code> if job was started
     * @since 1.1
     */
    private boolean isAborted() {
        return isStarted() && !isRunning() && !isPrematurlyEnded();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobProgress [jobId=").append(jobId)
                .append(", elapsedTime=").append(elapsedTime)
                .append(", progress=").append(progress).append(", note=")
                .append(note).append(", metadata=").append(metadata)
                .append(", recovery=").append(recovery)
                .append(", lastFailedJobStatus=").append(lastFailedJobStatus)
                .append(", jobContext=").append(jobContext)
                .append(", stopRequested=").append(stopRequested);
        return builder.toString();
    }

    /**
     * Notifies the job progress that a stop request was received.
     */
    @Override
    public void stopRequestReceived() {
        stopRequested = true;
        for (IJobProgressListener l : listeners) {
            l.jobStopping(this);
        }
    }
}
