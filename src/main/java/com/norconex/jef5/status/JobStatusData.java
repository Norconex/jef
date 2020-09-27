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
package com.norconex.jef5.status;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;

import com.norconex.commons.lang.EqualsUtil;
import com.norconex.commons.lang.map.Properties;


//TODO rename to IJobProgress? JobExecutionStatus, JobExecStatus
//or IJobReport?  IJobDetails?  IJobActivity? IJobActivityReport?
public class JobStatusData
        implements Serializable, Comparable<JobStatusData> {

    private static final long serialVersionUID = 1L;

    //TODO have this timeout here?  make it global and configurable? JobContext class?
    /** Activity timeout. */
    private static final long ACTIVITY_TIMEOUT = 10 * 1000;

    private double progress;
    private String note;
    private final Properties properties = new Properties();
    @ToStringExclude
    @HashCodeExclude
    @EqualsExclude
    private Instant lastActivity;
    private boolean stopRequested;

    private Instant startTime;
    //TODO is endTime required/useful, since lastActivity does it?
    private Instant endTime;

    /**
     * Gets the end time.
     * @return end time or <code>null</code> if the job has not ended
     */
    public final Instant getEndTime() {
        return endTime;
    }
    /**
     * Sets the end time.
     * @param endTime end time
     */
    public final void setEndTime(final Instant endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the start time.
     * @return start time or <code>null</code> if the job has not yet started
     */
    public final Instant getStartTime() {
        return startTime;
    }
    /**
     * Sets the start time.
     * @param startTime start time
     */
    public final void setStartTime(final Instant startTime) {
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
        if (isPrematurlyEnded())  { return JobState.UNCOMPLETED;  }
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
        Instant date = lastActivity;
        if (date == null) {
            return false;
        }
        return ChronoUnit.MILLIS.between(
                date, Instant.now()) < ACTIVITY_TIMEOUT;
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
    public Instant getLastActivity() {
        return lastActivity;
    }
    /**
     * Sets the last activity.
     * @param lastActivity last activity
     */
    public void setLastActivity(final Instant lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public int compareTo(JobStatusData o) {
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
        Properties otherProps = null;
        if (other != null) {
            otherProps = ((JobStatusData) other).properties;
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
