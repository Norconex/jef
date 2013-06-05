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
package com.norconex.jef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Job responsible for running a group of jobs asynchronously.  All jobs
 * are started at the same time, in different threads.  The progress of this
 * group reflects the average progress of all its jobs.  This job group
 * is considered completed and will only return when all its jobs are
 * done executing.  An exception in one job will not stop the other jobs
 * in the group from running.  On the other hand, one or more exception will
 * result in this group to fail.
 *
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public class AsyncJobGroup extends AbstractJobGroup {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(AsyncJobGroup.class);

    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param jobs jobs making up this group
     */
    public AsyncJobGroup(final String id, final IJob... jobs) {
        this(id, "Asynchronous job group with " + jobs.length + " jobs.", jobs);
    }
    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param jobs jobs making up this group
     * @param description job description
     */
    public AsyncJobGroup(
            final String id, String description, final IJob... jobs) {
        super(id, description, jobs);
    }

    @Override
    public void execute(
            final JobProgress progress, final JobSuite suite) {
        registerGroupProgressMonitoring(progress, suite);
        final Collection<IJob> failedJobs =
                Collections.synchronizedCollection(new ArrayList<IJob>());

        IJob[] jobs = getJobs();
        Thread[] threads = new Thread[jobs.length];
        for (int i = 0; i < jobs.length; i++) {
            final IJob job = jobs[i];
            threads[i] = new Thread(job.getId()) {
                @Override
                public void run() {
                    JobRunner runner = new JobRunner();
                    debug("Thread from " + AsyncJobGroup.this.getId()
                            + " started and about to run: " + job.getId());
                    if (!runner.runJob(job, suite)) {
                        synchronized (failedJobs) {
                            LOG.error(job.getId() + " failed.");
                            failedJobs.add(job);
                        }
                    } else {
                        debug(job.getId() + " succeeded.");
                    }
                    JobRunner.setCurrentJobId(AsyncJobGroup.this.getId());
                    debug("Thread from " + AsyncJobGroup.this.getId()
                            + " finished to run: " + job.getId());
                }
            };
            debug("Starting thread from " + getId()
                    + " for job: " + job.getId());
            threads[i].start();
        }

        // Wait for all threads to finish
        debug("Async group " + getId() + " waiting for all threads to finish.");
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            try {
                thread.join();
            } catch (InterruptedException e) {
                String err = "Error waiting for thread to finish for job "
                        + jobs[i];
                LOG.error(err, e);
                throw new JobException(err, e);
            }
        }
        debug("All threads finished for async group " + getId());

        unregisterGroupProgressMonitoring(suite);

        if (!failedJobs.isEmpty()) {
            throw new JobException(
                    failedJobs.size() + " out of " + jobs.length
                  + " jobs failed in async group " + getId());
        }

    }

    /**
     * Sends a debugging statement to class logger.
     * @param msg debugging message
     */
    private void debug(String msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(msg);
        }
    }
}
