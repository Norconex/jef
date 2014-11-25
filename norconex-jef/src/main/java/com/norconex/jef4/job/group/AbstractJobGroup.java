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
package com.norconex.jef4.job.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.norconex.jef4.job.IJob;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.JobStatusUpdater;
import com.norconex.jef4.suite.JobSuite;

/**
 * Base implementation for job groups.  The group progress is an average
 * of all job progress it contains.
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public abstract class AbstractJobGroup implements IJobGroup {

    /** Jobs that make up the group. */
    private final IJob[] jobs;
    /** For faster references caches the job ids. */
    private final List<String> jobIds;

    /** Job group unique identifier. */
    private String id;
    
    private GroupStatusUpdater groupUpdater;
    
    /**
     * Constructor.
     * @param id job unique identifier
     * @param jobs jobs to be run by the group
     */
    public AbstractJobGroup(
            final String id, final IJob... jobs) {
        super();
        if (id == null) {
            throw new IllegalArgumentException("Job id cannot be null");
        }
        this.id = id;
        if (jobs == null) {
            this.jobs = new IJob[] {};
        } else {
            this.jobs = jobs;
        }
        this.jobIds = new ArrayList<String>(this.jobs.length);
        for (int i = 0; i < this.jobs.length; i++) {
            jobIds.add(this.jobs[i].getId());
        }
    }

    @Override
    public final IJob[] getJobs() {
        return ArrayUtils.clone(jobs);
    }
    @Override
    public final String getId() {
        return id;
    }
    
    @Override
    public void execute(JobStatusUpdater statusUpdater, JobSuite suite) {
        groupUpdater = new GroupStatusUpdater(statusUpdater);
        executeGroup(suite);
    }

    public abstract void executeGroup(JobSuite suite);

    @Override
    public void groupProgressed(IJobStatus childJobStatus) {
        groupUpdater.childStatusChanged(childJobStatus);
    }
    
    @Override
    public void stop(IJobStatus status, JobSuite suite) {
        groupUpdater = null;
    }
    
    /*default*/ GroupStatusUpdater getGroupStatusUpdater() {
        return groupUpdater;
    }
    /*default*/ class GroupStatusUpdater {
        private final JobStatusUpdater statusUpdater;
        private double[] completionRatios = new double[jobs.length];
        public GroupStatusUpdater(JobStatusUpdater statusUpdater) {
            super();
            this.statusUpdater = statusUpdater;
        }
        public synchronized void childStatusChanged(IJobStatus status) {
            int jobIndex = jobIds.indexOf(status.getJobId());
            if (jobIndex >= 0) {
                completionRatios[jobIndex] = status.getProgress();
            }
            // Compute average
            double ratioTotal = 0;
            int completedCount = 0;
            for (int i = 0; i < completionRatios.length; i++) {
                if (completionRatios[i] >= 1.0d) {
                    completedCount++;
                }
                ratioTotal += completionRatios[i];
            }
            statusUpdater.setProgress(Math.min(1.0d,
                    (ratioTotal / (double) jobs.length)));
            statusUpdater.setNote(completedCount + " of "
                    + jobs.length + " jobs completed.");
        }
        
    }
    
}
