/* Copyright 2017 Norconex Inc.
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
package com.norconex.jef4.event;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.norconex.jef4.status.IJobStatus;

/**
 * A crawler event.
 * @author Pascal Essiembre
 * @see IJobEventListener
 */
public class JobEvent {

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
    
    private final String name;
    private final IJobStatus status;
    private final Object source;
    private final Throwable exception;

    /**
     * New crawler event.
     * @param name event name
     * @param status job status
     * @param source object responsible for triggering the event
     */
    public JobEvent(String name, IJobStatus status, Object source) {
        this(name, status, source, null);
    }

    
    /**
     * New crawler event.
     * @param name event name
     * @param status job status
     * @param source object responsible for triggering the event
     * @param exception exception
     */
    public JobEvent(String name, IJobStatus status, 
            Object source, Throwable exception) {
        super();
        this.name = name;
        this.status = status;
        this.source = source;
        this.exception = exception;
    }
    
    /**
     * Gets the object representing the source of this event.
     * @return the subject
     */
    public Object getSource() {
        return source;
    }

    /**
     * Gets the event name.
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the job status, if any.
     * @return the job status or <code>null</code>
     */
    public IJobStatus getStatus() {
        return status;
    }
    
    /**
     * Gets the exception, if any.
     * @return the exception or <code>null</code>
     */
    public Throwable getException() {
        return exception;
    }

    public boolean sameName(JobEvent event) {
        if (event == null) {
            return false;
        }
        return sameName(event.getName());
    }
    public boolean sameName(String eventName) {
        return Objects.equals(name, eventName);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof JobEvent))
            return false;
        JobEvent castOther = (JobEvent) other;
        return new EqualsBuilder()
                .append(name, castOther.name)
                .append(status, castOther.status)
                .append(source, castOther.source)
                .append(exception, castOther.exception)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(status)
                .append(source)
                .append(exception)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("status", status)
                .append("source", source)
                .append("exception", exception)
                .toString();
    }
}
