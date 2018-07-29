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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.jef5.JefException;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.suite.JobSuite;


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
public class SyncJobGroup extends AbstractJobGroup {

    /** Logger. */
    private static final Logger LOG =
            LoggerFactory.getLogger(SyncJobGroup.class);

    public SyncJobGroup(
            final String name, final IJob... jobs) {
        super(name, jobs);
    }

    @Override
    public void executeGroup(JobSuite suite) {

        IJob[] jobs = getJobs();
        String failedJob = null;
        for (int i = 0; i < jobs.length; i++) {
            LOG.debug("Synchronous group \"{}\" about to run synchronous "
                    + "job \"{}\".", getId(), jobs[i].getId());
            if (!suite.runJob(jobs[i])) {
                LOG.error("\"" + jobs[i].getId() + "\" failed.");
                failedJob = jobs[i].getId();
                break;
            }
            LOG.debug("\"{}\" succeeded.", jobs[i].getId());
        }
        if (failedJob != null) {
            throw new JefException(
                    "\"" + failedJob + "\" failed in sync group " + getId());
        }
    }
}
