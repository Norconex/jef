package com.norconex.jef.progress;

import java.io.Serializable;
import java.util.Date;

import com.norconex.jef.IJobContext;

/**
 * Representation of a job execution status at any given time.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 2.0
 */
public interface IJobStatus extends Serializable {
    
    /**
     * A job execution status.
     */
    enum Status{ 
        /**
         * The job was aborted (i.e. killed).  That is, if a job was started
         * and is no longer running, while it never was
         * marked as finished or stopped.  Under normal conditions, a job
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
         * are currently stopping.
         */
        STOPPING, 
        /**
         * A request to stop job execution has been received and the job
         * are stopped.
         */
        STOPPED
    };

    /**
     * The ID of the job this status represents.
     * @return
     */
    String getJobId();

    /**
     * Contextual information about the job this status represents.
     * @return job context information
     */
    IJobContext getJobContext();
    
    /**
     * Gets the current progress note.
     * @return Returns the note.
     */
    String getNote();

    /**
     * Gets the current progress.
     * @return Returns the progress.
     */
    long getProgress();

    /**
     * Gets the end date of this progress.  <code>null</code> if the progress
     * never finished.
     * @return end time
     */
    Date getEndTime();

    /**
     * Gets the date on which last activity on the job execution occured.
     * @return last activity date
     */
    Date getLastActivity();

    /**
     * Gets meta-data associated with this job progress.
     * @return meta-data
     */
    String getMetadata();

    /**
     * Gets the end start of this progress.  <code>null</code> if the progress
     * never started.
     * @return start time
     */
    Date getStartTime();

    /**
     * Whether a request stop this job was received.
     * @return <code>true</code> if a stop request was received.
     */
    boolean isStopRequested();
    
    /**
     * Gets how long it took to finish a job, in milliseconds.
     * @return job execution elapsed time
     */
    long getElapsedTime();
    

    /**
     * Gets the job completion ration.
     * @return a double between 0 and 1
     */
    double getCompletionRatio();

    /**
     * <p>Gets the current job status.</p>
     * @return job status
     */
    Status getStatus();
    
    /**
     * Whether this progress is from a recovery attempt from a previously 
     * failed job.
     * @return <code>true</code> if the current progress is a recovery
     */
    public boolean isRecovery();

    
}
