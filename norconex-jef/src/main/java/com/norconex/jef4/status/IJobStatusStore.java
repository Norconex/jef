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
package com.norconex.jef4.status;

import java.io.IOException;
import java.util.Date;

import com.norconex.commons.lang.config.IXMLConfigurable;

/**
 * Responsible for serializing and deserializing a job status.
 * <p/>
 * When saving as XML, the tag name must be called "jobStatusSerializer".
 * @author Pascal Essiembre
 */
public interface IJobStatusStore extends IXMLConfigurable {

    /**
     * Writes a job progress.
     * @param suiteName name space given to the job progress
     * @param jobStatus job progress
     * @throws IOException problem serializing job progress
     */
    void write(String suiteName, IJobStatus jobStatus)
              throws IOException;
    /**
     * Reads a job progress.  Implementors are required to always
     * return a job progress (<code>null</code> is not allowed).
     * @param suiteName name space given to the job progress
     * @param jobName job unique identifier
     * @param jobContext job context for which to obtain job progress
     * @return job progress
     * @throws IOException problem deserializing job progress
     */
    IJobStatus read(String suiteName, String jobName)
            throws IOException;
    /**
     * Removes job progress.  A removed job progress can no longer be
     * obtained using the {@link #read(String, String)} method.
     * @param suiteName name space given to the job progress
     * @param jobName unique identifier of job we want to remove status
     * @throws IOException problem removing job progress
     */
    void remove(String suiteName, String jobName) throws IOException;
    /**
     * Backups job progress.  A backed-up job progress can no longer be
     * obtained using the {@link #read(String, String)} method.
     * @param suiteName name space given to the job progress
     * @param jobId unique identifier of job progress we want to backup
     * @param backupDate date used to timestamp to backup
     * @throws IOException problem backing-up job progress
     */
    void backup(String suiteName, String jobId, Date backupDate)
            throws IOException;
    
    /**
     * Marks the status with the current date so it shows as being active.
     * @param suiteName suite name
     * @param jobName job name
     * @return new file timestamp as an EPOC long value
     * @throws IOException problem touching the file
     */
    long touch(String suiteName, String jobName) throws IOException;
}
