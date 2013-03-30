package com.norconex.jef;

/**
 * A job group is itself a job, with the added responsibility of managing
 * the execution of other jobs.  The progression tracking in a job group
 * is related to the progression of jobs it contains.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface IJobGroup extends IJob {

    /**
     * Gets all jobs part of this group.
     * @return jobs in the group
     */
    IJob[] getJobs();
}
