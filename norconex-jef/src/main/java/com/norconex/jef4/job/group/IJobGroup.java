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
