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
package com.norconex.jef.progress;

/**
 * Listener for job progress events.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface IJobProgressListener {

    /**
     * Invoked just before a job starts to stop.
     * @param progress job progress
     * @since 2.0
     */
    void jobStopping(IJobStatus progress);

    /**
     * Invoked just after has stopped.
     * @param progress job progress
     * @since 2.0
     */
    void jobStopped(IJobStatus progress);
    
    /**
     * Invoked just before a job begins its execution.
     * @param progress job progress
     */
    void jobStarted(IJobStatus progress);

    /**
     * Invoked just before a job resumes its execution.
     * @param progress job progress
     */
    void jobResumed(IJobStatus progress);

    /**
     * Invoked when the execution of a job gets skipped.  This may happen
     * when resuming a job suite and the job has already been completed.
     * @param progress job progress
     */
    void jobSkipped(IJobStatus progress);

    /**
     * Invoked every time the progress state changes.
     * @param progress job progress
     */
    void jobProgressed(IJobStatus progress);

    /**
     * Invoked after a job finished its execution before progress was 
     * 100% complete.
     * @param progress job progress
     * @since 2.0
     */
    void jobTerminatedPrematuraly(IJobStatus progress);

    /**
     * Invoked after a job normally finished its execution.  
     * @param progress job progress
     * @since 2.0
     */
    void jobCompleted(IJobStatus progress);
    
    /**
     * Invoked at regular intervals to confirm the job is still running.
     * This is separate than checking for a change of progress.  The progress
     * may not have changed between invocation, or may have changed multiple
     * times.
     * @param progress job progress
     */
    void jobRunningVerified(IJobStatus progress);
}
