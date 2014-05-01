package com.norconex.jef4.status;

public enum JobState {

    /**
     * The job was aborted (i.e. killed).  That is, if a job was started
     * and is no longer running, while it never was
     * marked as completed or stopped.  Under normal conditions, a job
     * should always finish, whether it failed or not.  An aborted
     * progress is usually the results of a job suite which got "killed"
     * in the middle of its execution (not having the chance to return
     * properly).
     */
    ABORTED,
    /**
     * The job execution has completed successfully.
     */
    COMPLETED,
    /**
     * The job stopped running on its own, but has not reached
     * 100% completion.
     */
    PREMATURE_TERMINATION,
    /**
     * The job is currently running.
     */
    RUNNING,
    /*STARTED,*/
    /**
     * Job execution status is unknown.  This status is returned when
     * there was no way to establish actual status, for unpredictable
     * reasons.
     */
    UNKNOWN,
    /**
     * A request to stop job execution has been received and the job
     * is currently stopping.
     */
    STOPPING,
    /**
     * A request to stop job execution has been received and the job
     * stopped.
     */
    STOPPED;
    
    public boolean isOneOf(JobState... jobStates) {
        for (JobState jobState : jobStates) {
            if (jobState == this) {
                return true;
            }
        }
        return false;
    }
}
