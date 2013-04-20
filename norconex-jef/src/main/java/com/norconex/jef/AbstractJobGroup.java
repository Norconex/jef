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
import java.util.List;

import com.norconex.jef.progress.IJobProgressListener;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.progress.JobProgressStateChangeAdapter;
import com.norconex.jef.suite.JobSuite;

/**
 * Base implementation for job groups.  The group progress is an average
 * of all job progress it contains, on a factor of 100.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
@SuppressWarnings("nls")
public abstract class AbstractJobGroup implements IJobGroup {

    /** Maximum job progress. */
    private static final long MAX_PROGRESS = 100;

    /** Jobs that make up the group. */
    private final IJob[] jobs;
    /** For faster references caches the job ids. */
    private final List<String> jobIds;

    /** Job group unique identifier. */
    private String id;
    /** Job group description. */
    private final String description;

    /** Listener for updating this group progress. */
    private IJobProgressListener progressListener;
    
    /**
     * Constructor.
     * @param id job unique identifier
     * @param jobs jobs to be run by the group
     */
    public AbstractJobGroup(
            final String id, final IJob[] jobs) {
        this(id, jobs, "Group of jobs.");
    }
    
    /**
     * Constructor.
     * @param id job unique identifier
     * @param jobs jobs to be run by the group
     * @param description job description
     */
    public AbstractJobGroup(
            final String id, final IJob[] jobs, final String description) {
        super();
        this.description = description;
        if (id == null) {
            throw new IllegalArgumentException("Job id cannot be null");
        }
        this.id = id;
        if (jobs == null) {
            this.jobs = new IJob[] {};
        } else {
            this.jobs = jobs;
        }
        this.jobIds = new ArrayList<String>(jobs.length);
        for (int i = 0; i < jobs.length; i++) {
            jobIds.add(jobs[i].getId());
        }
    }

    /**
     * @see com.norconex.jef.IJobGroup#getJobs()
     */
    public final IJob[] getJobs() {
        return jobs;
    }
    /**
     * @see com.norconex.jef.IJob#getId()
     */
    public final String getId() {
        return id;
    }
    public IJobContext createJobContext() {
        return new IJobContext() {
            private static final long serialVersionUID = -7997026549801055961L;
            public long getProgressMinimum() {
                return 0;
            }
            public long getProgressMaximum() {
                return MAX_PROGRESS;
            }
            public String getDescription() {
                return description;
            }
        }; 
    }
    
    /**
     * Registers a monitoring process so that individual job progress part
     * of this group gets reflected on the group overall progress.
     * @param groupProgress job progress for this group
     * @param suite suite for which we are tracking job progress
     */
    protected final void registerGroupProgressMonitoring(
            final JobProgress groupProgress, final JobSuite suite) {
        progressListener = new JobProgressStateChangeAdapter() {
            private double[] completionRatios = new double[jobs.length];
            public void jobSkipped(final IJobStatus progress) {
                jobStateChanged(progress);
            }
            public void jobStateChanged(final IJobStatus progress) {
                int jobIndex = jobIds.indexOf(progress.getJobId());
                if (jobIndex >= 0) {
                    completionRatios[jobIndex] = progress.getCompletionRatio();
                }
                // Compute average
                int ratioTotal = 0;
                int completedCount = 0;
                for (int i = 0; i < completionRatios.length; i++) {
                    if (completionRatios[i] >= 1.0) {
                        completedCount++;
                    }
                    ratioTotal += (int) Math.round(
                            completionRatios[i] * MAX_PROGRESS);
                }
                groupProgress.setProgress(Math.min(MAX_PROGRESS,
                        (long) (ratioTotal / jobs.length)));
                groupProgress.setNote(completedCount + " of "
                        + jobs.length + " jobs completed.");
            }
        };
        suite.addJobProgressListener(progressListener);
    }
    /**
     * Unregisters the monitoring process associated with the given job suite.
     * @param suite suite on which we were monitoring job progress
     */
    protected final void unregisterGroupProgressMonitoring(
            final JobSuite suite) {
        suite.removeJobProgressListener(progressListener);
        progressListener = null;
    }
    
    @Override
    public void stop(IJobStatus progress, final JobSuite suite) {
        // DOes nothing.  Default behaviour typically stops on its own
        // when all child jobs are stopped.
    }
}
