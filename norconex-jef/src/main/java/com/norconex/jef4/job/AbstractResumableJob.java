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
package com.norconex.jef4.job;

import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.JobStatusUpdater;
import com.norconex.jef4.suite.JobSuite;


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
        IJobStatus status = suite.getJobStatus(statusUpdater.getJobName());
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
