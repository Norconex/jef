package com.norconex.jef.progress;

import java.io.IOException;
import java.util.Date;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;

/**
 * Responsible for serializing a job progress.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public interface IJobProgressSerializer {

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
     * obtained using the {@link #deserialize(String, IJob)} method.
     * @param namespace name space given to the job progress
     * @param jobId unique identifier of job we want to remove status
     * @throws IOException problem removing job progress
     */
    void remove(String namespace, String jobId) throws IOException;
    /**
     * Backups job progress.  A backed-up job progress can no longer be
     * obtained using the {@link #deserialize(IJobContext)} method.
     * @param namespace name space given to the job progress
     * @param jobId unique identifier of job progress we want to backup
     * @param backupDate date used to timestamp to backup
     * @throws IOException problem backing-up job progress
     */
    void backup(String namespace, String jobId, Date backupDate)
            throws IOException;
}
