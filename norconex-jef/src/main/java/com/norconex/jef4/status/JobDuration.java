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

/**
 * Holds time-related information about a job execution.
 * @author Pascal Essiembre
 */
public class JobDuration {

    private Date resumedStartTime;
    private Date resumedLastActivity;
    private long resumedDuration;
    
    
//    /** One second in milliseconds. */
//    private static final int SECOND = 1000;
//    /** One minute in milliseconds. */
//    private static final int MINUTE = 60 * SECOND;
//    /** One hour in milliseconds. */
//    private static final int HOUR = 60 * MINUTE;
//    /** One day in milliseconds. */
//    private static final int DAY = 24 * HOUR;

    private Date startTime;
    private Date endTime;
    
    //TODO move lastActivity to IJobStatus:


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
        return endTime;
    }
    /**
     * Sets the end time.
     * @param endTime
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
     * @param startTime
     */
    public final void setStartTime(final Date startTime) {
        this.startTime = startTime;
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
        return -1;
    }
    
    /**
     * Gets the elapsed time in milliseconds between the start date of the 
     * first elapsed time and the end of the last elapsed time.  If there
     * are no previous elapsed time, this method has the same effect 
     * as calling {@link #getDuration()}.
     * @return the total elapsed time
     */
    public final long getFullDuration() {
        if (startTime != null && endTime != null) {
            return endTime.getTime() - startTime.getTime();
        }
        return -1;
    }

    public Date getResumedStartTime() {
        return resumedStartTime;
    }

    public void setResumedStartTime(Date resumedStartTime) {
        this.resumedStartTime = resumedStartTime;
    }

    public Date getResumedLastActivity() {
        return resumedLastActivity;
    }

    public void setResumedLastActivity(Date resumedLastActivity) {
        this.resumedLastActivity = resumedLastActivity;
    }

    public long getResumedDuration() {
        return resumedDuration;
    }

    public void setResumedDuration(long resumedDuration) {
        this.resumedDuration = resumedDuration;
    }
    
    
    
//    public class TimeRange implements Serializable {
//        private static final long serialVersionUID = -4230903051960691337L;
//        private Date start;
//        private Date end;
//        public Date getStart() {
//            return start;
//        }
//        public void setStart(Date fromDate) {
//            this.start = fromDate;
//        }
//        public Date getEnd() {
//            return end;
//        }
//        public void setEnd(Date toDate) {
//            this.end = toDate;
//        }
//        public long getDuration() {
//            if (start == null || end == null) {
//                return -1;
//            }
//            return end.getTime() - start.getTime();
//        }
//    }
    
//    /**
//     * Gets the elapsed time as a string, in a human readable format.
//     * @return elapsed time
//     */
//    @SuppressWarnings("nls")
//    public final String getElapsedTimeAsString() {
//        long time = getDuration();
//        if (time < 0) {
//            return "";
//        }
//        String str = ""; 
//        int days = (int) time / DAY;
//        time -= days * DAY;
//        if (days > 0) {
//            str += days + "d"; 
//        }
//        int hours = (int) time / HOUR;
//        time -= hours * HOUR;
//        if (hours > 0) {
//            str += hours + "h"; 
//        }
//        int minutes = (int) time / MINUTE;
//        time -= minutes * MINUTE;
//        if (minutes > 0) {
//            str += minutes + "m"; 
//        }
//        int seconds = (int) time / SECOND;
//        time -= seconds * SECOND;
//        if (seconds > 0) {
//            str += seconds + "s"; 
//        }
//        if (str.equals("")) { 
//            str += time + "ms"; 
//        }
//        return str;
//    }
//    


}
