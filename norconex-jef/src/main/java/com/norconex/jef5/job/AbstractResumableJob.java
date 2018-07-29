/* Copyright 2010-2018 Norconex Inc.
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
package com.norconex.jef5.job;

import com.norconex.jef5.status.JobStatus;
import com.norconex.jef5.status.JobStatusUpdater;
import com.norconex.jef5.suite.JobSuite;


/**
 * <p>Convenience class separating normal execution from recovery.  If the job
 * is starting clean, the <code>startExecution</code> method will get invoked.
 * Else, provided that there is already progress on a starting job
 * and the job is incomplete (i.e. failure), the <code>resumeExecution</code>
 * method will be invoked.</p>
 *
 * <p>Typical usage of this class might be when extra (or different) steps
 * need to be undertaken when resuming a job.  Otherwise, there may be no
 * benefits to using this class over a straight implementation of
 * <code>IJob</code>.</p>
 * 
 * @author Pascal Essiembre
 */
public abstract class AbstractResumableJob implements IJob {

    /**
     * Constructor.
     */
    public AbstractResumableJob() {
        super();
    }

    @Override
    public void execute(JobStatusUpdater statusUpdater, JobSuite suite) {
        JobStatus status = suite.getJobStatus(statusUpdater.getJobId());
        if (!status.isResumed()) {
            startExecution(statusUpdater, suite);
        } else if (!status.isCompleted()) {
            resumeExecution(statusUpdater, suite);
        }
    }

    /**
     * Starts the execution of a job.
     * @param statusUpdater job progress
     * @param suite job suite
     */
    protected abstract void startExecution(
            JobStatusUpdater statusUpdater, JobSuite suite);
    /**
     * Resumes the execution of a job.
     * @param statusUpdater job progress
     * @param suite job suite
     */
    protected abstract void resumeExecution(
            JobStatusUpdater statusUpdater, JobSuite suite);
}
