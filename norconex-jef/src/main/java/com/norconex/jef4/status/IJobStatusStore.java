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
package com.norconex.jef4.status;

import java.io.IOException;
import java.util.Date;

import com.norconex.commons.lang.config.IXMLConfigurable;

/**
 * Responsible for serializing and deserializing a job status.
 * <p/>
 * When saving as XML, the tag name must be called "statusStore".
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
     * @param jobId job unique identifier
     * @param jobContext job context for which to obtain job progress
     * @return job progress
     * @throws IOException problem deserializing job progress
     */
    IJobStatus read(String suiteName, String jobId)
            throws IOException;
    /**
     * Removes job progress.  A removed job progress can no longer be
     * obtained using the {@link #read(String, String)} method.
     * @param suiteName name space given to the job progress
     * @param jobId unique identifier of job we want to remove status
     * @throws IOException problem removing job progress
     */
    void remove(String suiteName, String jobId) throws IOException;
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
     * @param jobId job name
     * @return new file timestamp as an EPOC long value
     * @throws IOException problem touching the file
     */
    long touch(String suiteName, String jobId) throws IOException;
}
