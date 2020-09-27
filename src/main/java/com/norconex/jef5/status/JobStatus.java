/* Copyright 2018 Norconex Inc.
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

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;



//TODO rename to IJobProgress? JobExecutionStatus, JobExecStatus
//or IJobReport?  IJobDetails?  IJobActivity? IJobActivityReport?
public class JobStatus extends JobStatusData {

    private static final long serialVersionUID = 1L;

    //TODO have status Comparable by JobDuration (startDate)
    private final String jobId;

    //TODO remove the keeping of resumed attempts.
    @ToStringExclude
    @HashCodeExclude
    @EqualsExclude
    private final Set<JobStatusData> resumedAttempts = new TreeSet<>();

    public JobStatus(String jobId, Set<JobStatusData> resumedAttempts) {
        this.jobId = jobId;
        if (resumedAttempts != null) {
            this.resumedAttempts.addAll(resumedAttempts);
        }
    }

    public String getJobId() {
        return jobId;
    }
    public Set<JobStatusData> getResumedAttempts() {
        return resumedAttempts;
    }

    /**
     * Whether this status resumed from a previously
     * failed or stopped job.
     * @return <code>true</code> if the current job was resumed
     */
    public boolean isResumed() {
        return !resumedAttempts.isEmpty();
    }

    /**
     * Gets the start time of the oldest resumed attempt,
     * or the current start time if there were no previous attempt.
     * @return status start time or <code>null</code> if never started
     */
    public Instant getSessionStartTime() {
        if (resumedAttempts.isEmpty()) {
            return getStartTime();
        }
        return resumedAttempts.iterator().next().getStartTime();
    }
    //TODO have sessionEndTime? will always be same as endTime.

    // Start date of oldest resumed instance until end date of most recent
    // attempt or last activity date.
    // If never resumed, same as calling #getDuration().
    public Duration getSessionDuration() {
        Instant start = getSessionStartTime();
        Instant end = ObjectUtils.defaultIfNull(
                getEndTime(), getLastActivity());
        if (start != null && end != null) {
            return Duration.between(start, end);
        }
        return Duration.ZERO;
    }

    // Combine the elapsed time of all resumed job plus this one to give
    // the cumulated time jobs have run as opposed to calendar-duration.
    public Duration getSessionEffectiveDuration() {
        Duration duration = getDuration();
        if (resumedAttempts.isEmpty()) {
            return duration;
        }
        for (JobStatusData js : resumedAttempts) {
            duration = duration.plus(js.getDuration());
        }
        return duration;
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
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
