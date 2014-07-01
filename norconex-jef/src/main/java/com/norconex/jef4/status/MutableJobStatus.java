package com.norconex.jef4.status;

import java.util.Date;

import com.norconex.commons.lang.map.Properties;

public class MutableJobStatus implements IJobStatus {

    private static final long serialVersionUID = -3106380316055356588L;

    /** Activity timeout. */
    private static final long ACTIVITY_TIMEOUT = 10 * 1000;
    
    private final String jobName;
    private double progress;
    private String note;
    private int resumeAttempts;
    private JobDuration duration = new JobDuration();
    private Properties properties = new Properties();
    private Date lastActivity;
    private boolean stopRequested;

    public MutableJobStatus(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobName() {
        return jobName;
    }


    /**
     * Gets the job execution state.
     * @return execution state
     */
    @Override
    public JobState getState() {

        // The order is important to establish status
        if (isAborted())   { return JobState.ABORTED;   }
        if (isStopped())   { return JobState.STOPPED;   }
        if (isStopping())   { return JobState.STOPPING;   }
        if (isCompleted()) { return JobState.COMPLETED; }
        if (isPrematurlyEnded())  { return JobState.PREMATURE_TERMINATION;  }
        if (isRunning())   { return JobState.RUNNING;   }
        return JobState.UNKNOWN;
    }

    
    @Override
    public boolean isStopped() {
        return stopRequested && !isRunning();
    }
    
    @Override
    public boolean isStopping() {
        return stopRequested && isRunning();
    }

    public boolean isStopRequested() {
        return stopRequested;
    }
    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }

    /**
     * Whether this status resumed from a previously 
     * failed or stopped job.
     * @return <code>true</code> if the current job was resumed
     * @since 1.1.1
     */
    @Override
    public boolean isResumed() {
        return resumeAttempts > 0;
    }
    
    /**
     * Checks whether the job was started or not.  This is not an indication
     * that a job is currently running.
     * @return <code>true</code> if job was started
     */
    @Override
    public boolean isStarted() {
        return duration.getStartTime() != null;
    }
    /**
     * Checks whether the job has before its time.  This is not an indication
     * that a job was completed or that there were no errors.
     * @return <code>true</code> if job finished
     */
    @Override
    public boolean isPrematurlyEnded() {
        return duration.getEndTime() != null && !isCompleted();
    }
    /**
     * Checks whether the job execution has completed.
     * @return <code>true</code> if the job execution is complete
     */
    @Override
    public boolean isCompleted() {
        return progress >= 1d;
    }
    /**
     * Checks whether the job was aborted or not (i.e. killed).  That is, if
     * the job started and is no longer running, while it never
     * marked as finished.  Remember that under normal conditions, a job
     * should always finish, whether it failed or not.  An aborted progress
     * is usually the results of a job suite which got "killed" in the middle
     * of its execution (not having the chance to return properly).
     * @return <code>true</code> if job was started
     * @since 1.1
     */
    @Override
    public boolean isAborted() {
        return isStarted() && !isRunning() 
                && duration.getEndTime() == null 
                && !isCompleted();
    }
    
    /**
     * Checks whether the job execution represented by this progress is still
     * running.
     * @return <code>true</code> if still running
     */
    @Override
    public boolean isRunning() {
        Date date = lastActivity;
        if (date == null) {
            return false;
        }
//        if (isPrematurlyEnded()) {
//                return false;
//        }
        return (System.currentTimeMillis() - date.getTime())
                < ACTIVITY_TIMEOUT;
    }
    
    /**
     * Checks whether the current progress status matches any of the supplied
     * statuses.
     * @param states one or more states to match
     * @return <code>true</code> if progress status matches supplied statuses
     * @since 2.0
     */
    @Override
    public boolean isState(JobState... states) {
        JobState thisState = getState();
        for (JobState state : states) {
            if (thisState == state) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public JobDuration getDuration() {
        return duration;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public void setDuration(JobDuration duration) {
        this.duration = duration;
    }

    /**
     * Gets the last activity.
     * @return last activity
     */
    public Date getLastActivity() {
        return lastActivity;
    }
    /**
     * Sets the last activity.
     * @param lastActivity last activity
     */
    public void setLastActivity(final Date lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    @Override
    public int getResumeAttempts() {
        return resumeAttempts;
    }
    public void setResumeAttempts(int resumeAttempts) {
        this.resumeAttempts = resumeAttempts;
    }
    
    public void incrementResumeAttempts() {
        resumeAttempts++;        
    }
}
