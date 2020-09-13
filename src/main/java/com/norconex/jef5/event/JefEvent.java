/* Copyright 2018-2020 Norconex Inc.
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

import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;

import com.norconex.commons.lang.event.Event;
import com.norconex.jef5.status.JobStatus;

/**
 * A crawler event.
 * @author Pascal Essiembre
 */
public class JefEvent extends Event {

    private static final long serialVersionUID = 1L;

    public static final String SUITE_STARTED = "SUITE_STARTED";
    public static final String SUITE_STOPPING = "SUITE_STOPPING";
    public static final String SUITE_STOPPED = "SUITE_STOPPED"; // <-- Realy an event? If stopped, it means nothing else is running, now even listeners?.
    public static final String SUITE_ABORTED = "SUITE_ABORTED"; // <-- Realy an event? If abborted, no event can be sent.
    //TODO eliminate this one and rely on COMPLETED + status to find if failed/success?
    //TODO have a SUITE_FAILED instead (or in addition??)
    //TODO make SUITE_INCOMPLETE ?
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

    public static class Builder extends Event.Builder<Builder> {

        private JobStatus status;

        public Builder(String name, Object source) {
            super(name, source);
        }

        public Builder status(JobStatus status) {
            this.status = status;
            return this;
        }

        @Override
        public JefEvent build() {
            return new JefEvent(this);
        }
    }

    /**
     * New event.
     * @param b builder
     */
    protected JefEvent(Builder b) {
        super(b);
        this.status = b.status;
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
        return EqualsBuilder.reflectionEquals(this, other);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(
                this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString());
        if (status != null) {
            b.append("progress", NumberUtils.toScaledBigDecimal(
                    status.getProgress(), 2, RoundingMode.FLOOR));
            b.append("note", status.getNote());
        }
        return b.toString();
    }
}
