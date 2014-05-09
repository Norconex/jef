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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.config.ConfigurationLoader;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.commons.lang.map.Properties;
import com.norconex.jef4.JEFUtil;
import com.norconex.jef4.job.JobException;

/**
 * Serializer using a file to store job status information. The created
 * file name matches the job id, plus the ".job" extension.  The path
 * where to locate the file depends on the constructor invoked.
 *
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public class FileJobStatusStore implements IJobStatusStore {

    private static final long serialVersionUID = 132626905287944939L;

    private static final Logger LOG =
            LogManager.getLogger(FileJobStatusStore.class);

    private String jobdirLatest;
    private String jobdirBackupBase;
    private String statusDir;

    
    public FileJobStatusStore() {
        super();
    }
    /**
     * Creates a file-based job status serializer storing files in the given
     * job directory.
     * @param statusDir the base directory where to serialize the job status
     */
    public FileJobStatusStore(final String statusDir) {
        this.statusDir = statusDir;
        resolveDirs();
    }
    
    public String getStatusDirectory() {
        return statusDir;
    }
    public void setStatusDirectory(String statusDirectory) {
        this.statusDir = statusDirectory;
        resolveDirs();
    }

    private void resolveDirs() {
        String path = statusDir;
        if (StringUtils.isBlank(statusDir)) {
            LOG.info("No status directory specified.");
            path = JEFUtil.FALLBACK_WORKDIR.getAbsolutePath();
        }
        LOG.info("Status serialization directory: " + path); 
        jobdirLatest = path + File.separatorChar 
                + "latest" + File.separatorChar + "status";
        jobdirBackupBase = path + "/backup";
        File dir = new File(jobdirLatest);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public final void write(
            String suiteName, final IJobStatus jobStatus)
            throws IOException {

        File file = getStatusFile(suiteName, jobStatus.getJobName());
        if (!file.exists()) {
            file.createNewFile();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Serializing file: " + file);
        }
        Properties config = new Properties();
        config.setString("jobName", jobStatus.getJobName());
//        config.setLong("minimum", jobContext.getProgressMinimum());
//        config.setLong("maximum", jobContext.getProgressMaximum());
        config.setDouble("progress", jobStatus.getProgress());

//        if (jobStatus.isStopRequested()) {
//            config.setBoolean("stop", true);
//        }
        if (jobStatus.getNote() != null) {
            config.setString("note", jobStatus.getNote());
        }
//        if (jobStatus.getMetadata() != null) {
//            config.setString("metadata", jobStatus.getMetadata());
//        }
//        if (jobStatus.getStartTime() != null) {
//            config.setDate("startTime", jobStatus.getStartTime());
//        }
//        if (jobStatus.getEndTime() != null) {
//            config.setDate("endTime", jobStatus.getEndTime());
//        }
        Properties props = jobStatus.getProperties();
        for (String key : props.keySet()) {
            config.put("prop." + key, props.get(key));
        }
        
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        config.store(os, "Progress for job: " + jobStatus.getJobName());
        os.close();
    }

    @Override
    public final IJobStatus read(
            String suiteName, final String jobName)
            throws IOException {
        File file = getStatusFile(suiteName, jobName);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deserializing file: " + file);
        }
        if (file.exists()) {
            MutableJobStatus jobStatus = new MutableJobStatus(jobName);
            
            
//            JobDuration elapsedTime = new JobDuration();
//            progress = new JobProgress(jobId, jobContext, elapsedTime);
            Properties config = new Properties();
            InputStream is = new FileInputStream(file);
            config.load(is);
            if (LOG.isDebugEnabled()) {
                LOG.debug(jobName + " last active time: "
                        + new Date(file.lastModified()));
            }
//            elapsedTime.setLastActivity(new Date(file.lastModified()));
//            elapsedTime.setStartTime(config.getDate("startTime"));
//            elapsedTime.setEndTime(config.getDate("endTime"));
//            progress.setMetadata(config.getString("metadata"));
            jobStatus.setNote(config.getString("note"));
//            if (config.getBoolean("stop", false)) {
//                progress.stopRequestReceived();
//            }
            jobStatus.setProgress(config.getDouble("progress", 0d));
            
            Properties props = jobStatus.getProperties();
            for (String key : config.keySet()) {
                if (key.startsWith("prop.")) {
                    props.put(StringUtils.removeStart(
                            "prop.", key), props.get(key));
                }
            }
            is.close();
            return jobStatus;
        }
        return new MutableJobStatus(jobName);
    }

    @Override
    public final void remove(final String suiteName, final String jobId)
            throws IOException {
        File file = getStatusFile(suiteName, jobId);
        file.delete();
        if (file.exists()) {
            throw new IOException("Unable to delete file: " + file);
        }
    }

    @Override
    public final void backup(
            final String suiteName, final String jobId, final Date backupDate)
            throws IOException {
        File progressFile = getStatusFile(suiteName, jobId);
        File backupFile = getBackupFile(suiteName, jobId, backupDate);
        progressFile.renameTo(backupFile);
    }

    @Override
    public long touch(String suiteName, String jobName) throws IOException {
        File file = getStatusFile(suiteName, jobName);
        FileUtils.touch(file);
        return file.lastModified();
    }
    
    /**
     * Gets the file used to store the job progress.
     * @param suiteName name space given to the job progress
     * @param jobName the job unique name
     * @return file used to store the job process
     */
    private File getStatusFile(final String suiteName, final String jobName) {
        return new File(jobdirLatest 
                + "/" + FileUtil.toSafeFileName(suiteName)
                + "__" + FileUtil.toSafeFileName(jobName) + ".job");
    }
    /**
     * Gets the file used to store the job progress backup.
     * @param suiteName name space given to the job progress
     * @param jobName the id of the job
     * @param backupDate date used to timestamp to backup
     * @return file used to store the job process
     */
    private File getBackupFile(final String suiteName, final String jobName, 
            final Date backupDate) {
        String date = new SimpleDateFormat(
                "yyyyMMddHHmmssSSSS").format(backupDate);
        File backupDir;
        try {
            backupDir = FileUtil.createDateDirs(
                    new File(jobdirBackupBase), backupDate);
        } catch (IOException e) {
            throw new JobException("Could not create backup directory for "
                    + "job \"" + jobName + "\".");
        }
        backupDir = new File(backupDir, "status");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        return new File(backupDir + "/" + date + "__" 
                + FileUtil.toSafeFileName(suiteName)
                + "__" + FileUtil.toSafeFileName(jobName) + ".job");
    }

    @Override
    public void loadFromXML(Reader in) throws IOException {
        XMLConfiguration xml = ConfigurationLoader.loadXML(in);
        setStatusDirectory(xml.getString("statusDir", statusDir));
    }

    @Override
    public void saveToXML(Writer out) throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);
            writer.writeStartElement("jobStatusSerializer");
            writer.writeAttribute("class", getClass().getCanonicalName());
            writer.writeStartElement("statusDir");
            writer.writeCharacters(statusDir);
            writer.writeEndElement();
            writer.writeEndElement();
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IOException("Cannot save as XML.", e);
        }       
    }

}
