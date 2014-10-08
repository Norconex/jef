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
package com.norconex.jef4.status;

import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;

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
}
