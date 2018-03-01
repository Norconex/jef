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
package com.norconex.jef5.session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;



//TODO rename to IJobProgress? JobExecutionStatus, JobExecStatus
//or IJobReport?  IJobDetails?  IJobActivity? IJobActivityReport?
public class JobSession extends JobSessionData {

    private static final long serialVersionUID = 1L;

    //TODO have status Comparable by JobDuration (startDate)
    private final String jobId;
    private final Set<JobSessionData> resumedAttempts = new TreeSet<>();
    
    public JobSession(String jobId, Set<JobSessionData> resumedAttempts) {
        this.jobId = jobId;
        this.resumedAttempts.addAll(resumedAttempts);
    }
    
    public String getJobId() {
        return jobId;
    }
    public Set<JobSessionData> getResumedAttempts() {
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

    public LocalDateTime getSessionStartTime() {
        if (resumedAttempts.isEmpty()) {
            return getStartTime();
        }
        return resumedAttempts.iterator().next().getStartTime();
    }
    //TODO have sessionEndTime? will always be same as endTime.
    
    // Start date of first resumed job until end date or last activity date.
    // If never resumed, same as calling #getDuration().
    public Duration getSessionDuration() {
        LocalDateTime start = getSessionStartTime();
        LocalDateTime end = ObjectUtils.defaultIfNull(
                getEndTime(), getLastActivity());
        if (end != null) {
            return Duration.between(start, end);
        }
        return Duration.ZERO;
    }
    
    // Combine the elapsed time of all resumed job plus this one to give
    // the cummulated time jobs have run as opposed to calendar-duration.
    public Duration getSessionActiveDuration() {
        if (resumedAttempts.isEmpty()) {
            return getDuration();
        }
        Duration d = getDuration();
        for (JobSessionData js : resumedAttempts) {
            d = d.plus(js.getDuration());
        }
        return d;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof JobSession)) {
            return false;
        }
        JobSession castOther = (JobSession) other;
        return new EqualsBuilder()
                .appendSuper(super.equals(castOther))
                .append(jobId, castOther.jobId)
                .append(resumedAttempts, castOther.resumedAttempts)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(super.hashCode())
                .append(jobId)
                .append(resumedAttempts)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("name", jobId)
                .append("resumedAttempts", resumedAttempts)
                .toString();
    }        
}
