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
 * Listener for job status events.
 * @author Pascal Essiembre
 */
public interface IJobLifeCycleListener {

    /**
     * Invoked just before a job starts to stop.
     * @param status job status
     * @since 2.0
     */
    void jobStopping(IJobStatus status);

    /**
     * Invoked just after has stopped.
     * @param status job status
     * @since 2.0
     */
    void jobStopped(IJobStatus status);
    
    /**
     * Invoked just before a job begins its execution.
     * @param status job status
     */
    void jobStarted(IJobStatus status);

    /**
     * Invoked just before a job resumes its execution.
     * @param status job status
     */
    void jobResumed(IJobStatus status);

    /**
     * Invoked when the execution of a job gets skipped.  This may happen
     * when resuming a job suite and the job has already been completed.
     * @param status job status
     */
    void jobSkipped(IJobStatus status);

    /**
     * Invoked every time the status state changes.
     * @param status job status
     */
    void jobProgressed(IJobStatus status);

    /**
     * Invoked after a job finished its execution before status was 
     * 100% complete.
     * @param status job status
     * @since 2.0
     */
    void jobTerminatedPrematuraly(IJobStatus status);

    /**
     * Invoked after a job normally finished its execution.  
     * @param status job status
     * @since 2.0
     */
    void jobCompleted(IJobStatus status);
}
