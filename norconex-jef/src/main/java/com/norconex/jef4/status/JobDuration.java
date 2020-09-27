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
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Holds time-related information about a job execution.
 * @author Pascal Essiembre
 */
public class JobDuration {

    private Date resumedStartTime;
    private Date resumedLastActivity;

    private Date startTime;
    private Date endTime;

    /**
     * Creates a job elapsed time.
     */
    public JobDuration() {
        super();
    }

    /**
     * Gets the end time.
     * @return end time
     */
    public final Date getEndTime() {
        return ObjectUtils.clone(endTime);
    }
    /**
     * Sets the end time.
     * @param endTime end time
     */
    public final void setEndTime(final Date endTime) {
        this.endTime = ObjectUtils.clone(endTime);
    }

    /**
     * Gets the start time.
     * @return start time
     */
    public final Date getStartTime() {
        return ObjectUtils.clone(startTime);
    }
    /**
     * Sets the start time.
     * @param startTime start time
     */
    public final void setStartTime(final Date startTime) {
        this.startTime = ObjectUtils.clone(startTime);
    }

    /**
     * Gets the elapsed time in milliseconds between the execution start
     * and end time.
     * @return elapsed time
     */
    public final long getDuration() {
        if (startTime != null && endTime != null) {
            return endTime.getTime() - startTime.getTime();
        }
        return 0;
    }

    /**
     * Gets the elapsed time in milliseconds between the start date of the
     * first elapsed time and the end of the last elapsed time.  If there
     * are no previous elapsed time, this method has the same effect
     * as calling {@link #getDuration()}.
     * @return the total elapsed time
     */
    public final long getTotalDuration() {
        return getDuration() + getResumedDuration();
    }

    public Date getResumedStartTime() {
        return ObjectUtils.clone(resumedStartTime);
    }

    public void setResumedStartTime(Date resumedStartTime) {
        this.resumedStartTime = ObjectUtils.clone(resumedStartTime);
    }

    public Date getResumedLastActivity() {
        return ObjectUtils.clone(resumedLastActivity);
    }

    public void setResumedLastActivity(Date resumedLastActivity) {
        this.resumedLastActivity = ObjectUtils.clone(resumedLastActivity);
    }

    public long getResumedDuration() {
        if (resumedStartTime != null && resumedLastActivity != null) {
            return resumedLastActivity.getTime() - resumedStartTime.getTime();
        }
        return 0;
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
