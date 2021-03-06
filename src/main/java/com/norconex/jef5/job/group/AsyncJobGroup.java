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
package com.norconex.jef5.job.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.jef5.JefException;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.suite.JobSuite;

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
public class AsyncJobGroup extends AbstractJobGroup {

    /** Logger. */
    private static final Logger LOG =
            LoggerFactory.getLogger(AsyncJobGroup.class);

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
     * @param jobs jobs making up this group
     * @since 2.0.0
     */
    public AsyncJobGroup(String id, List<? extends IJob> jobs) {
        this(id, jobs.size(), jobs);
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
        this(id, maxThreads, Arrays.asList(jobs));
    }
    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param maxThreads maximum number of threads (jobs) executing at the
     *        same time
     * @param jobs jobs making up this group
     * @since 2.0.0
     */
    public AsyncJobGroup(
            final String id, int maxThreads, final List<? extends IJob> jobs) {
        super(id, jobs);
        this.maxThread = maxThreads;
    }

    @Override
    public void executeGroup(final JobSuite suite) {
        final Collection<IJob> failedJobs =
                Collections.synchronizedCollection(new ArrayList<>());
        List<IJob> jobs = getJobs();
        int realMaxThread = Math.min(maxThread, jobs.size());
        final CountDownLatch latch = new CountDownLatch(jobs.size());
        ExecutorService pool = Executors.newFixedThreadPool(realMaxThread);
        for (final IJob job : jobs) {
            pool.execute(() -> runJob(job, suite, failedJobs, latch));
        }

        try {
            latch.await();
            pool.shutdown();
        } catch (InterruptedException e) {
             throw new JefException(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("All threads finished for async group\""
                    + getId() + "\".");
        }

        if (!failedJobs.isEmpty()) {
            throw new JefException(
                    failedJobs.size() + " out of " + jobs.size()
                  + " jobs failed in async group \"" + getId() + "\"");
        }
    }

    private void runJob(IJob job, JobSuite suite, Collection<IJob> failedJobs,
            CountDownLatch latch) {
        Thread.currentThread().setName(job.getId());
        JobSuite.setCurrentJobId(job.getId());

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
