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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.io.FileUtil;
import com.norconex.commons.lang.map.Properties;
import com.norconex.jef.IJobContext;

/**
 * Serializer using a file to store job process information. The created
 * file name matches the job id, plus the ".job" extension.  The path
 * where to locate the file depends on the constructor invoked.
 *
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public class JobProgressPropertiesFileSerializer
        implements IJobProgressSerializer {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(JobProgressPropertiesFileSerializer.class);

    /** Directory where to store the file. */
    private String jobdirProgress;
    /** Directory where to backup the file. */
    private String jobdirBackup;

    /**
     * Creates a file-based job progress serializer storing files in the given
     * job directory.
     * @param jobDir the base directory where to serialize the job progress
     */
    public JobProgressPropertiesFileSerializer(final String jobDir) {
        jobdirProgress = jobDir + "/latest";
        jobdirBackup = jobDir + "/backup";
        File dir = new File(jobdirProgress);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = new File(jobdirBackup);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public final void serialize(
            String namespace, final IJobStatus jobProgress)
            throws IOException {

        IJobContext jobContext = jobProgress.getJobContext(); 
        File file = getProgressFile(namespace, jobProgress.getJobId());
        if (!file.exists()) {
            file.createNewFile();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Serializing file: " + file);
        }
        Properties config = new Properties();
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        config.setString("jobId", jobProgress.getJobId());
        config.setLong("minimum", jobContext.getProgressMinimum());
        config.setLong("maximum", jobContext.getProgressMaximum());
        config.setLong("progress", jobProgress.getProgress());
        if (jobProgress.isStopRequested()) {
            config.setBoolean("stop", true);
        }
        if (jobProgress.getNote() != null) {
            config.setString("note", jobProgress.getNote());
        }
        if (jobProgress.getMetadata() != null) {
            config.setString("metadata", jobProgress.getMetadata());
        }
        if (jobProgress.getStartTime() != null) {
            config.setDate("startTime", jobProgress.getStartTime());
        }
        if (jobProgress.getEndTime() != null) {
            config.setDate("endTime", jobProgress.getEndTime());
        }
        config.store(os, "Progress for job: " + jobProgress.getJobId());
        os.close();
    }

    @Override
    public final JobProgress deserialize(
            String namespace, final String jobId, final IJobContext jobContext)
            throws IOException {
        File file = getProgressFile(namespace, jobId);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deserializing file: " + file);
        }
        if (file.exists()) {
            JobProgress progress = null;
            JobElapsedTime elapsedTime = new JobElapsedTime();
            progress = new JobProgress(jobId, jobContext, elapsedTime);
            Properties config = new Properties();
            InputStream is = new FileInputStream(file);
            config.load(is);
            if (LOG.isDebugEnabled()) {
                LOG.debug(jobId + " last active time: "
                        + new Date(file.lastModified()));
            }
            elapsedTime.setLastActivity(new Date(file.lastModified()));
            elapsedTime.setStartTime(config.getDate("startTime"));
            elapsedTime.setEndTime(config.getDate("endTime"));
            progress.setMetadata(config.getString("metadata"));
            progress.setNote(config.getString("note"));
            if (config.getBoolean("stop", false)) {
                progress.stopRequestReceived();
            }
            progress.setProgress(
                    config.getLong("progress", progress.getProgress()));
            is.close();
            return progress;
        }
        return null;
    }

    @Override
    public final void remove(final String namespace, final String jobId)
            throws IOException {
        File file = getProgressFile(namespace, jobId);
        file.delete();
        if (file.exists()) {
            throw new IOException("Unable to delete file: " + file);
        }
    }

    @Override
    public final void backup(
            final String namespace, final String jobId, final Date backupDate)
            throws IOException {
        File progressFile = getProgressFile(namespace, jobId);
        File backupFile = getBackupFile(namespace, jobId, backupDate);
        progressFile.renameTo(backupFile);
    }

    /**
     * Gets the file used to store the job progress.
     * @param namespace name space given to the job progress
     * @param jobId the id of the job
     * @return file used to store the job process
     */
    private File getProgressFile(final String namespace, final String jobId) {
        return new File(
                jobdirProgress + "/" + namespace + "__" + jobId + ".job");
    }
    /**
     * Gets the file used to store the job progress backup.
     * @param namespace name space given to the job progress
     * @param jobId the id of the job
     * @param backupDate date used to timestamp to backup
     * @return file used to store the job process
     */
    private File getBackupFile(
            final String namespace, final String jobId, final Date backupDate) {
        String date = new SimpleDateFormat(
                "yyyyMMddHHmmssSSSS").format(backupDate);
        try {
            return new File(FileUtil.createDateDirs(
                    new File(jobdirBackup), backupDate)
                    + "/" + date + "__" + namespace + "__" + jobId + ".job");
        } catch (IOException e) {
            return new File(jobdirBackup + "/" + date + "__"
                    + namespace + "__" + jobId + ".job");
        }
    }
}
