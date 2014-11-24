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
package com.norconex.jef4.job;

import com.norconex.jef4.status.IJobStatus;

/**
 * Allows one to "visit" a job suite and walk its job hierarchy with 
 * minimal effort.  
 * @author Pascal Essiembre
 * @since 1.1
 */
public interface IJobVisitor {
    /**
     * Visits a job.
     * @param job job visited
     * @param jobStatus status of visited job
     */
    void visitJob(IJob job, IJobStatus jobStatus);
}
