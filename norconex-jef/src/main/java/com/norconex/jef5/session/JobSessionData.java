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
package com.norconex.jef5.session;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.norconex.commons.lang.map.Properties;


//TODO rename to IJobProgress? JobExecutionStatus, JobExecStatus
//or IJobReport?  IJobDetails?  IJobActivity? IJobActivityReport?
public class JobSessionData 
        implements Serializable, Comparable<JobSessionData> {

    private static final long serialVersionUID = 1L;

    //TODO have this timeout here?  make it global and configurable? JobContext class?
    /** Activity timeout. */
    private static final long ACTIVITY_TIMEOUT = 10 * 1000;
    
    private double progress;
    private String note;
    private Properties properties = new Properties();
    private LocalDateTime lastActivity;
    private boolean stopRequested;

    private LocalDateTime startTime;
    //TODO is endTime required/useful, since lastActivity does it?
    private LocalDateTime endTime;
    
    /**
     * Gets the end time.
     * @return end time
     */
    public final LocalDateTime getEndTime() {
        return endTime;
    }
    /**
     * Sets the end time.
     * @param endTime end time
     */
    public final void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the start time.
     * @return start time
     */
    public final LocalDateTime getStartTime() {
        return startTime;
    }
    /**
     * Sets the start time.
     * @param startTime start time
     */
    public final void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the job execution state.
     * @return execution state
     */
    public JobState getState() {

        // The order is important to establish state
        if (isAborted())   { return JobState.ABORTED;   }
        if (isStopped())   { return JobState.STOPPED;   }
        if (isStopping())   { return JobState.STOPPING;   }
        if (isCompleted()) { return JobState.COMPLETED; }
        if (isPrematurlyEnded())  { return JobState.PREMATURE_TERMINATION;  }
        if (isRunning())   { return JobState.RUNNING;   }
        return JobState.UNKNOWN;
    }

    public boolean isStopped() {
        return stopRequested && !isRunning();
    }
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
     * Checks whether the job was started or not.  This is not an indication
     * that a job is currently running.
     * @return <code>true</code> if job was started
     */
    
    public boolean isStarted() {
        return getStartTime() != null;
    }
    /**
     * Checks whether the job ended before its time. This may or may not
     * be the result of an error.  Prematurely ended jobs are eligible
     * for resuming.
     * @return <code>true</code> if job finished
     */
    //TODO fix method typo (PrematurEly).
    
    public boolean isPrematurlyEnded() {
        return getEndTime() != null && !isCompleted();
    }
    /**
     * Checks whether the job execution has completed.
     * @return <code>true</code> if the job execution is complete
     */
    
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
    
    public boolean isAborted() {
        return isStarted() && !isRunning() 
                && getEndTime() == null 
                && !isCompleted();
    }
    
    /**
     * Checks whether the job execution represented by this progress is still
     * running.
     * @return <code>true</code> if still running
     */
    
    public boolean isRunning() {
        LocalDateTime date = lastActivity;
        if (date == null) {
            return false;
        }
        return ChronoUnit.MILLIS.between(
                date, LocalDateTime.now()) < ACTIVITY_TIMEOUT;
    }
    
    /**
     * Checks whether the current progress status matches any of the supplied
     * statuses.
     * @param states one or more states to match
     * @return <code>true</code> if progress status matches supplied statuses
     */
    public boolean isState(JobState... states) {
        JobState thisState = getState();
        for (JobState state : states) {
            if (thisState == state) {
                return true;
            }
        }
        return false;
    }
    
    
    public double getProgress() {
        return progress;
    }

    
    public String getNote() {
        return note;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    /**
     * Gets the null-safe duration between the execution start
     * and the end time, or last activity time if the end time is 
     * <code>null</code>. If start time or both end time and the last
     * activity time are not set, the duration will be zero.
     * @return elapsed time
     */
    public final Duration getDuration() {
        if (startTime != null && (endTime != null || lastActivity != null)) {
            return Duration.between(startTime, 
                    ObjectUtils.defaultIfNull(endTime, lastActivity));
        }
        return Duration.ZERO;
    }
    
    /**
     * Gets the last activity.
     * @return last activity
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    /**
     * Sets the last activity.
     * @param lastActivity last activity
     */
    public void setLastActivity(final LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    @Override
    public int compareTo(JobSessionData o) {
        if (startTime == null && o.startTime == null) {
            return 0;
        }
        if (startTime != null && o.startTime == null) {
            return 1;
        }
        if (startTime == null && o.startTime != null) {
            return -1;
        }
        return getStartTime().compareTo(o .getStartTime());
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof JobSessionData)) {
            return false;
        }
        JobSessionData castOther = (JobSessionData) other;
        return new EqualsBuilder()
                .append(progress, castOther.progress)
                .append(note, castOther.note)
                .append(properties, castOther.properties)
                .append(lastActivity, castOther.lastActivity)
                .append(stopRequested, castOther.stopRequested)
                .append(startTime, castOther.startTime)
                .append(endTime, castOther.endTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(progress)
                .append(note)
                .append(properties)
                .append(lastActivity)
                .append(stopRequested)
                .append(startTime)
                .append(endTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("progress", progress)
                .append("note", note)
                .append("properties", properties)
                .append("lastActivity", lastActivity)
                .append("stopRequested", stopRequested)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .toString();
    }    
}
