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
package com.norconex.jef5.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.norconex.commons.lang.event.Event;
import com.norconex.jef5.status.JobStatus;

/**
 * A crawler event.
 * @author Pascal Essiembre
 * @see IJefEventListener
 */
public class JefEvent extends Event<Object> {

    public static final String SUITE_STARTED = "SUITE_STARTED";
    public static final String SUITE_STOPPING = "SUITE_STOPPING";
    public static final String SUITE_STOPPED = "SUITE_STOPPED";
    public static final String SUITE_ABORTED = "SUITE_ABORTED";
    //TODO eliminate this one and rely on COMPLETED + status to find if failed/success?
    //TODO have a SUITE_FAILED instead (or in addition??)
    public static final String SUITE_TERMINATED_PREMATURALY = "SUITE_TERMINATED_PREMATURALY";
    public static final String SUITE_COMPLETED = "SUITE_COMPLETED";

    //TODO have SUITE_RESUMED???
    
    
    // These ones replace IJobLifeCycleListener
    public static final String JOB_STARTED = "JOB_STARTED";
    public static final String JOB_RESUMED = "JOB_RESUMED";
    public static final String JOB_PROGRESSED = "JOB_PROGRESSED";
    public static final String JOB_SKIPPED = "JOB_SKIPPED";
    public static final String JOB_STOPPING = "JOB_STOPPING";
    public static final String JOB_STOPPED = "JOB_STOPPED";
    public static final String JOB_COMPLETED = "JOB_COMMPLETED";
    //TODO eliminate this one and rely on COMPLETED + status to find if failed/success?
    //TODO have a JOB_FAILED instead (or in addition??)
    public static final String JOB_TERMINATED_PREMATURALY = "JOB_TERMINATED_PREMATURALY";

    // When an error occured, which is different than when a job failed
    // one can keep going, the other one stops???????
    // this one replaces IJobErrorListener
    public static final String JOB_ERROR = "JOB_ERROR";
    
    private final JobStatus status;

    /**
     * New crawler event.
     * @param name event name
     * @param status job status
     * @param source object responsible for triggering the event
     */
    public JefEvent(String name, JobStatus status, Object source) {
        this(name, status, source, null);
    }

    
    /**
     * New crawler event.
     * @param name event name
     * @param status job status
     * @param source object responsible for triggering the event
     * @param exception exception
     */
    public JefEvent(String name, JobStatus status, 
            Object source, Throwable exception) {
        super(name, source, exception);
        this.status = status;
    }
    
    /**
     * Gets the job status, if any.
     * @return the job status or <code>null</code>
     */
    public JobStatus getStatus() {
        return status;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof JefEvent))
            return false;
        JefEvent castOther = (JefEvent) other;
        return new EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(status, castOther.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(status)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("status", status)
                .toString();
    }
}
