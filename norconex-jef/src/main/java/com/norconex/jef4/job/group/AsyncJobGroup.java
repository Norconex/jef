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
package com.norconex.jef4.job.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef4.JEFException;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.JobException;
import com.norconex.jef4.suite.JobSuite;

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

    private final int maxThread;
    
    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param jobs jobs making up this group
     */
    public AsyncJobGroup(final String id, final IJob... jobs) {
        this(id, jobs.length, jobs);
    }
    
    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param maxThreads maximum number of threads (jobs) executing at the
     *        same time
     * @param jobs jobs making up this group
     */
    public AsyncJobGroup(
            final String id, int maxThreads, final IJob... jobs) {
        super(id, jobs);
        this.maxThread = maxThreads;
    }

    @Override
    public void executeGroup(final JobSuite suite) {
        final Collection<IJob> failedJobs =
                Collections.synchronizedCollection(new ArrayList<IJob>());
        IJob[] jobs = getJobs();
        int realMaxThread = Math.min(maxThread, jobs.length);
        final CountDownLatch latch = new CountDownLatch(jobs.length);
        ExecutorService pool = Executors.newFixedThreadPool(realMaxThread);
        for (final IJob job : jobs) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    runJob(job, suite, failedJobs, latch);
                }
            });
        }

        try {
            latch.await();
            pool.shutdown();
        } catch (InterruptedException e) {
             throw new JEFException(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("All threads finished for async group\"" 
                    + getId() + "\".");
        }

        if (!failedJobs.isEmpty()) {
            throw new JobException(
                    failedJobs.size() + " out of " + jobs.length
                  + " jobs failed in async group \"" + getId() + "\"");
        }
    }
    
    private void runJob(IJob job, JobSuite suite, Collection<IJob> failedJobs,
            CountDownLatch latch) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread from " + AsyncJobGroup.this.getId()
                    + " started and about to run: " + job.getId());
            }
            if (!suite.runJob(job)) {
                synchronized (failedJobs) {
                    LOG.error(job.getId() + " failed.");
                    failedJobs.add(job);
                }
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(job.getId() + " succeeded.");
            }
            JobSuite.setCurrentJobId(AsyncJobGroup.this.getId());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread from " + AsyncJobGroup.this.getId()
                    + " finished to run: " + job.getId());
            }
        } finally {
            latch.countDown();
        }
        
    }
}
