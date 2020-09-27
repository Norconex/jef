/* Copyright 2010-2020 Norconex Inc.
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
package com.norconex.jef4.status;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;

import com.norconex.commons.lang.EqualsUtil;
import com.norconex.commons.lang.map.Properties;

public class MutableJobStatus implements IJobStatus {

    /** Activity timeout. */
    private static final long ACTIVITY_TIMEOUT = 10 * 1000;

    private final String jobId;
    private double progress;
    private String note;
    private int resumeAttempts;
    private JobDuration duration = new JobDuration();
    private Properties properties = new Properties();
    @ToStringExclude
    @HashCodeExclude
    @EqualsExclude
    private Date lastActivity;
    private boolean stopRequested;

    public MutableJobStatus(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String getJobId() {
        return jobId;
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
     * Checks whether the job ended before its time. This may or may not
     * be the result of an error.  Prematurely ended jobs are eligible
     * for resuming.
     * @return <code>true</code> if job finished
     */
    //TODO fix method typo (PrematurEly).
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
    @Override
    public Date getLastActivity() {
        return ObjectUtils.clone(lastActivity);
    }
    /**
     * Sets the last activity.
     * @param lastActivity last activity
     */
    public void setLastActivity(final Date lastActivity) {
        this.lastActivity = ObjectUtils.clone(lastActivity);
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

    @Override
    public boolean equals(final Object other) {
        Properties otherProps = null;
        if (other != null) {
            otherProps = ((MutableJobStatus) other).properties;
        }
        return EqualsBuilder.reflectionEquals(this, other, "properties")
                 &&  EqualsUtil.equalsMap(properties, otherProps);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
