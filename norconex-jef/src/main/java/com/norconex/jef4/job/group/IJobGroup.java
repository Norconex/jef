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

import com.norconex.jef4.job.IJob;
import com.norconex.jef4.status.IJobStatus;


/**
 * A job group is itself a job, with the added responsibility of managing
 * the execution of other jobs.  The progression tracking in a job group
 * is related to the progression of jobs it contains.
 * @author Pascal Essiembre
 */
public interface IJobGroup extends IJob {

    /**
     * Gets all jobs part of this group.
     * @return jobs in the group
     */
    IJob[] getJobs();
    
    
    void groupProgressed(IJobStatus childJobStatus);
}
