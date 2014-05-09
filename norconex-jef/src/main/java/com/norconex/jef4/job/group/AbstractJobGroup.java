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
import java.util.List;

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
    private final List<String> jobNames;

    /** Job group unique identifier. */
    private String name;
    
    private GroupStatusUpdater groupUpdater;
    
    /**
     * Constructor.
     * @param name job unique identifier
     * @param jobs jobs to be run by the group
     */
    public AbstractJobGroup(
            final String name, final IJob... jobs) {
        super();
        if (name == null) {
            throw new IllegalArgumentException("Job name cannot be null");
        }
        this.name = name;
        if (jobs == null) {
            this.jobs = new IJob[] {};
        } else {
            this.jobs = jobs;
        }
        this.jobNames = new ArrayList<String>(jobs.length);
        for (int i = 0; i < jobs.length; i++) {
            jobNames.add(jobs[i].getName());
        }
    }

    @Override
    public final IJob[] getJobs() {
        return jobs;
    }
    @Override
    public final String getName() {
        return name;
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
            int jobIndex = jobNames.indexOf(status.getJobName());
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
