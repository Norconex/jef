/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef4.job;

import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.suite.JobSuite;

/**
 * Event thrown by the framework to all registered <code>ErrorHandler</code>
 * instances.
 * @author Pascal Essiembre
 */
public class JobErrorEvent {

    private final Throwable t;
    private final JobSuite suite;
    private final IJobStatus status;
    
    public JobErrorEvent(Throwable t, JobSuite suite, IJobStatus status) {
        super();
        this.t = t;
        this.suite = suite;
        this.status = status;
    }

    /**
     * Gets the exception behind this error.
     * @return the exception
     */
    public Throwable getException() {
        return t;
    }
    /**
     * Gets the job suite from which this error occurred.
     * @return job suite
     */
    public JobSuite getSuite() {
        return suite;
    }
    /**
     * Gets the job progress of the job from which the error got triggered.
     * @return job progress
     */
    public IJobStatus getStatus() {
        return status;
    }

}
