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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.jef.JobException;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.suite.JobSuite;


/**
 * Job responsible for running a group of jobs synchronously.  Jobs are
 * executed in the order specified by the <code>IJob[]</code> array.
 * The progress of this
 * group reflects the average progress of all its jobs.  This job group
 * is considered completed and will only return when all its jobs are
 * done executing.  An exception in one job will stop the other jobs
 * in the group from executing.  Such an exception will
 * result in this group to fail.
 *
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public class SyncJobGroup extends AbstractJobGroup {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(SyncJobGroup.class);

    public SyncJobGroup(
            final String name, final IJob... jobs) {
        super(name, jobs);
    }

    @Override
    public void executeGroup(JobSuite suite) {

        IJob[] jobs = getJobs();
        String failedJob = null;
        for (int i = 0; i < jobs.length; i++) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Synchronous group \"" + getName() + "\" "
                        + "about to run synchronous job \""
                        + jobs[i].getName() + "\".");
            }
            if (!suite.runJob(jobs[i])) {
                LOG.error("\"" + jobs[i].getName() + "\" failed.");
                failedJob = jobs[i].getName();
                break;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("\"" + jobs[i].getName() + "\" succeeded.");
            }
        }
        if (failedJob != null) {
            throw new JobException(
                    "\"" + failedJob + "\" failed in sync group " + getName());
        }
        //JobSuite.setCurrentJobId(getName());
    }
}
