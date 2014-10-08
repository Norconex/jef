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
