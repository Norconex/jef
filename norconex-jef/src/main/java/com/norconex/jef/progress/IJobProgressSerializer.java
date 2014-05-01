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
package com.norconex.jef.progress;

import java.io.IOException;
import java.util.Date;

import com.norconex.commons.lang.config.IXMLConfigurable;
import com.norconex.jef.IJobContext;

/**
 * Responsible for serializing a job progress.
 * @author Pascal Essiembre
 */
public interface IJobProgressSerializer extends IXMLConfigurable {

    /**
     * Serializes a job progress.
     * @param namespace name space given to the job progress
     * @param jobProgress job progress
     * @throws IOException problem serializing job progress
     */
    void serialize(String namespace, IJobStatus jobProgress)
              throws IOException;
    /**
     * Deserializes a job progress.  Implementors are required to always
     * return a job progress (<code>null</code> is not allowed).
     * @param namespace name space given to the job progress
     * @param jobId job unique identifier
     * @param jobContext job context for which to obtain job progress
     * @return job progress
     * @throws IOException problem deserializing job progress
     */
    JobProgress deserialize(
            String namespace, String jobId, IJobContext jobContext)
            throws IOException;
    /**
     * Removes job progress.  A removed job progress can no longer be
     * obtained using the {@link #deserialize(String, String, IJobContext)} 
     * method.
     * @param namespace name space given to the job progress
     * @param jobId unique identifier of job we want to remove status
     * @throws IOException problem removing job progress
     */
    void remove(String namespace, String jobId) throws IOException;
    /**
     * Backups job progress.  A backed-up job progress can no longer be
     * obtained using the {@link #deserialize(String, String, IJobContext)}
     * method.
     * @param namespace name space given to the job progress
     * @param jobId unique identifier of job progress we want to backup
     * @param backupDate date used to timestamp to backup
     * @throws IOException problem backing-up job progress
     */
    void backup(String namespace, String jobId, Date backupDate)
            throws IOException;
}
