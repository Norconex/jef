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
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Job responsible for running a group of jobs asynchronously with only so many
 * jobs running at the same time.  The maximum number of job is started and
 * once one is completed, another is started.  The progress of this
 * group reflects the average progress of all its jobs.  This job group
 * is considered completed and will only return when all its jobs are
 * done executing.  An exception in one job will not stop the other jobs
 * in the group from running.  On the other hand, one or more exception will
 * result in this group to fail.
 *
 * @since 1.1.1
 * @author David Gaulin (david.gaulin&#x40;norconex.com)
 */
@SuppressWarnings("nls")
public class AsyncLimitedJobGroup extends AsyncJobGroup {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(AsyncJobGroup.class);

    /** Max Number of Jobs */
    private int maxNumberOfRunningJobs = 0;
    
    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param jobs jobs making up this group
     */
    public AsyncLimitedJobGroup(
            final String id, int maxNumberOfRunningJobs, final IJob... jobs) {
        this(id, maxNumberOfRunningJobs, "Asynchronous job group with " 
                + jobs.length + " jobs.", jobs);
    }
    /**
     * Constructor.
     * @param id unique identifier for this job group
     * @param jobs jobs making up this group
     */
    public AsyncLimitedJobGroup(
            final String id,  int maxNumberOfRunningJobs, 
            String description, final IJob... jobs) {
        super(id, description, jobs);
        this.maxNumberOfRunningJobs = maxNumberOfRunningJobs;
    }

    @Override
    public final void execute(
            final JobProgress progress, final JobSuite suite) {
         registerGroupProgressMonitoring(progress, suite);

        final Collection<IJob> failedJobs =
            Collections.synchronizedCollection(new ArrayList<IJob>());

        
        IJob[] jobs = getJobs();
        List<Thread> threads = launchLimitedJobThreads(suite, failedJobs);
        boolean allJobStarted = threads.size() >= jobs.length;
        int totalNotAlive = 0;
        while (!allJobStarted) {
            int notAlive = 0;
            for (int x = 0; x < threads.size(); x++) {
                Thread thread = (Thread) threads.get(x);
                if (!thread.isAlive()) {
                    notAlive++;
                }
            }
            for (int x = 0; x < notAlive - totalNotAlive 
                && threads.size() < jobs.length; x++) {
                final IJob job = jobs[threads.size()];
                Thread t = createJobThread(job, suite, failedJobs);
                t.start();
                threads.add(t);
            }
            if (threads.size() == jobs.length) {
                allJobStarted = true;
            }
            totalNotAlive = notAlive;
            Sleeper.sleepSeconds(1);
        }

        // Wait for all threads to finish
        debug("Asynclimited group " + getId() 
                + " waiting for all threads to finish.");
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                String err = "Error waiting for thread to finish for job "
                        + jobs[threads.indexOf(thread)];
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

    private List<Thread> launchLimitedJobThreads(
            final JobSuite suite, Collection<IJob> failedJobs) {
        List<Thread> threads = new ArrayList<Thread>();
        IJob[] jobs = getJobs();
        for (int x = 0; x < jobs.length && x < maxNumberOfRunningJobs; x++) {
            final IJob job = jobs[x];
            Thread t = createJobThread(job, suite, failedJobs);
            debug("Starting thread from " + getId()
                    + " for job: " + job.getId());
            t.start();
            threads.add(t);
        }
        return threads;
    }
    
    
    private Thread createJobThread(final IJob job, final JobSuite suite,
            final Collection<IJob> failedJobs) {
        return new Thread(job.getId()) {
            public void run() {
                JobRunner runner = new JobRunner();
                debug("Thread from " + AsyncLimitedJobGroup.this.getId()
                        + " started and about to run: " + job.getId());
                if (!runner.runJob(job, suite)) {
                    synchronized (failedJobs) {
                        LOG.error(job.getId() + " failed.");
                        failedJobs.add(job);
                    }
                } else {
                    debug(job.getId() + " succeeded.");
                }
                JobRunner.setCurrentJobId(AsyncLimitedJobGroup.this.getId());
                debug("Thread from " + AsyncLimitedJobGroup.this.getId()
                        + " finished to run: " + job.getId());
            }
        };
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
