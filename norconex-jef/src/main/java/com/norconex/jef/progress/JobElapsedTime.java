/* Copyright 2010-2013 Norconex Inc.
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
package com.norconex.jef.progress;

import java.util.Date;

/**
 * Holds time-related information about a job execution.
 * @author Pascal Essiembre
 */
public class JobElapsedTime {

    /** One second in milliseconds. */
    private static final int SECOND = 1000;
    /** One minute in milliseconds. */
    private static final int MINUTE = 60 * SECOND;
    /** One hour in milliseconds. */
    private static final int HOUR = 60 * MINUTE;
    /** One day in milliseconds. */
    private static final int DAY = 24 * HOUR;

    /** Execution start time. */
    private Date startTime;
    /** Execution end time. */
    private Date endTime;
    /** Last activity. */
    private Date lastActivity;

    /**
     * Creates a job elapsed time.
     */
    public JobElapsedTime() {
        super();
    }
    
    /**
     * Creates a job elapsed time, initialized.
     * @param startTime start time
     * @param endTime end time
     * @param lastActivity last job activity
     * @since 1.1.1
     */
    public JobElapsedTime(Date startTime, Date endTime, Date lastActivity) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastActivity = lastActivity;
    }

    /**
     * Gets the end time.
     * @return end time
     */
    public final Date getEndTime() {
        return endTime;
    }
    /**
     * Sets the end time.
     * @param endTime end time
     */
    public final void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the start time.
     * @return start time
     */
    public final Date getStartTime() {
        return startTime;
    }
    /**
     * Sets the start time.
     * @param startTime start time
     */
    public final void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the last activity.
     * @return last activity
     */
    public final Date getLastActivity() {
        return lastActivity;
    }
    /**
     * Sets the last activity.
     * @param lastActivity last activity
     */
    public final void setLastActivity(final Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    /**
     * Gets the elapsed time in milliseconds between the execution start
     * and end time.
     * @return elapsed time
     */
    public final long getElapsedTime() {
        if (startTime != null && endTime != null) {
            return endTime.getTime() - startTime.getTime();
        }
        return -1;
    }
    /**
     * Gets the elapsed time as a string, in a human readable format.
     * @return elapsed time
     */
    @SuppressWarnings("nls")
    public final String getElapsedTimeAsString() {
        long time = getElapsedTime();
        if (time < 0) {
            return "";
        }
        String str = ""; 
        int days = (int) time / DAY;
        time -= days * DAY;
        if (days > 0) {
            str += days + "d"; 
        }
        int hours = (int) time / HOUR;
        time -= hours * HOUR;
        if (hours > 0) {
            str += hours + "h"; 
        }
        int minutes = (int) time / MINUTE;
        time -= minutes * MINUTE;
        if (minutes > 0) {
            str += minutes + "m"; 
        }
        int seconds = (int) time / SECOND;
        time -= seconds * SECOND;
        if (seconds > 0) {
            str += seconds + "s"; 
        }
        if (str.equals("")) { 
            str += time + "ms"; 
        }
        return str;
    }
}
