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

import com.norconex.jef4.suite.JobSuite;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.JobStatusUpdater;

/**
 * <p>A job to be executed by the Job Execution Framework.
 * Implementors are responsible for reporting job progress and errors in a
 * uniform way.  Great care should be taken to make every jobs recoverable.</p>
 *
 * <p>Jobs are usually assembled together to form a <code>JobSuite</code>.
 * Progress tracking, error handling, logging, etc., are all handled by job
 * suites and implementors do not have to worry about these concerns when
 * creating jobs.</p>
 *
 * <p>To ensure the best integration possible with the framework, implementors
 * are invited to adhere to the following practices:
 * <ul>
 *   <li>update job progress on <code>JobProgress</code>;
 *   <li>ensure jobs are recoverable by considering the current progress
 *       passed to the execute method;
 *   <li>use Log4J for all logging purposes;
 *   <li>wrap exceptions you explicitly want to be handled by the framework
 *       into a runtime <code>JobException</code> instance;
 *   <li>use lazy-loading where possible
 * </ul>
 *
 * @author Pascal Essiembre
 */
public interface IJob {

    
    /**
     * Gets the job unique identifier. All characters are valid and regular
     * words can be used, as long as the returned string is unique.
     * @return job unique identifier
     */
    String getId();

    /**
     * Executes this job.  Implementors are responsible for updating
     * execution progress on the given <code>JobProgress</code>.
     * @param statusUpdater status updater
     * @param suite job suite this job is part of
     */
    void execute(final JobStatusUpdater statusUpdater, final JobSuite suite);

    /**
     * Stops this job.  Implementors are responsible for terminating
     * the execution of this job.  The progress and other contextual 
     * information can be set, but the "status" should not 
     * be overwritten, as the framework will take care of assigning it.
     * @param status current job status
     * @param suite job suite this job is part of
     * @since 2.0
     */
    void stop(final IJobStatus status, final JobSuite suite);

}
