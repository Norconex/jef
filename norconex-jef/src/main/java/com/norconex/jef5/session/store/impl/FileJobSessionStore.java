/* Copyright 2010-2018 Norconex Inc.
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
package com.norconex.jef5.session.store.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.config.IXMLConfigurable;
import com.norconex.commons.lang.config.XMLConfigurationUtil;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.commons.lang.map.Properties;
import com.norconex.commons.lang.time.DateUtil;
import com.norconex.commons.lang.xml.EnhancedXMLStreamWriter;
import com.norconex.jef5.JefException;
import com.norconex.jef5.event.IJefEventListener;
import com.norconex.jef5.event.JefEvent;
import com.norconex.jef5.session.JobSession;
import com.norconex.jef5.session.JobSessionData;
import com.norconex.jef5.session.store.IJobSessionStore;

/**
 * <p>
 * File-based status store. The created
 * file name matches the job id, plus the ".job" extension. If no 
 * status directory is explicitly set, it defaults to: 
 * <code>&lt;user.home&gt;/Norconex/jef/workdir</code>
 * </p>
 * 
 * <h3>XML configuration usage:</h3>
 * <pre>
 *  &lt;statusStore class="com.norconex.jef4.status.FileJobStatusStore"&gt;
 *      &lt;statusDir&gt;(directory where to store status files)&lt;/statusDir&gt;
 *  &lt;/statusStore&gt;
 * </pre>
 * <h4>Usage example:</h4>
 * <p>
 * The following example indicates status files should be stored in this 
 * directory:
 * <code>/tmp/jefstatuses</code>
 * </p>
 * <pre>
 *  &lt;statusStore class="com.norconex.jef4.status.FileJobStatusStore"&gt;
 *      &lt;statusDir&gt;/tmp/jefstatuses&lt;/statusDir&gt;
 *  &lt;/statusStore&gt;
 * </pre>
 * 
 * @author Pascal Essiembre
 */
public class FileJobSessionStore 
        implements IJobSessionStore, IJefEventListener, IXMLConfigurable {

    //TODO if event listener specified, register the listener.
    private static final Logger LOG =
            LoggerFactory.getLogger(FileJobSessionStore.class);

    //TODO have default location to be suite "workdir"
    public static final Path DEFAULT_STORE_PATH = Paths.get(".");
    private static boolean atomicMoveFailed; 
    private Path storeDir;
    private Path storeBackupDir;
    
    
//TODO no backup dir == delete each time (no backup)
    public FileJobSessionStore() {
        this(DEFAULT_STORE_PATH);
    }
    public FileJobSessionStore(Path storeDir) {
        super();
        this.storeDir = storeDir; 
    }
    
    public Path getStoreDir() {
        return storeDir;
    }
    public void setStoreDir(Path storeDir) {
        this.storeDir = storeDir;
    }

    public Path getStoreBackupDir() {
        return storeBackupDir;
    }
    public void setStoreBackupDir(Path storeBackupDir) {
        this.storeBackupDir = storeBackupDir;
    }

    @Override
    public final void write(String suiteName, final JobSession js)
            throws IOException {

        //The JobSessionData should not be written/read here??? so rename arg to JobSession?
        
        Properties config = new Properties();
        config.setString("jobId", js.getJobId());
        config.setDouble("progress", js.getProgress());
        config.setString("note", js.getNote());
        config.setLocalDateTime("startTime", js.getStartTime());
        config.setLocalDateTime("endTime", js.getEndTime());
        
        //TODO store different status for stopping and stopped?
        if (js.isStopping() || js.isStopped()) {
            config.setBoolean("stopRequested", true);
        }
        Properties props = js.getProperties();
        for (Entry<String, List<String>> entry : props.entrySet()) {
            config.put("." + entry.getKey(), entry.getValue());
        }
        Path file = resolveDataFile(suiteName, js.getJobId());
        LOG.trace("Writing status file: {}", file);
        
//        synchronized(js) {
            //TODO consider using SeekableByteChannel or else from NIO?
            
            // Using RandomAccessFile since evidence has shown it is better at 
            // dealing with files/locks in a way that cause less/no errors.
            // "d" ensures content is all written before a read.
            try (RandomAccessFile ras = 
                    new RandomAccessFile(file.toFile(), "rwd");
                    FileChannel channel = ras.getChannel();
                    FileLock lock = channel.lock()) {
                StringWriter sw = new StringWriter();
                config.store(sw);
                ras.writeUTF(sw.toString());
            }
//        }
    }

    @Override
    public final JobSession read(String suiteName, final String jobId)
            throws IOException {

        Set<JobSessionData> attempts = new TreeSet<>();
        Path file = null;
        int attemptNo = 1;
        while ((file = resolveDataFile(
                suiteName, jobId, attemptNo++)).toFile().exists()) {
            JobSessionData data = new JobSessionData();
            read(data, file);
            attempts.add(data);
        }
        JobSession jobSession = new JobSession(jobId, attempts);
        read(jobSession, resolveDataFile(suiteName, jobId));
        return jobSession;
    }

    private final void read(final JobSessionData jsd, final Path file)
            throws IOException {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading session file: " + file);
        }
        if (/*!file.toFile().exists() || */ Files.size(file) == 0) {
            jsd.setLastActivity(LocalDateTime.from(
                    Files.getLastModifiedTime(file).toInstant()));
            return;
        }

        Properties config = new Properties();
        
        //TODO consider using SeekableByteChannel or else from NIO?
        // Is the following working (locks)?
//      config.load(new ByteArrayInputStream(Files.readAllBytes(file)));
        
        // Using RandomAccessFile since evidence has shown it is better at 
        // dealing with files/locks in a way that cause less/no errors.
        try (RandomAccessFile ras = new RandomAccessFile(file.toFile(), "r")) {
            StringReader sr = new StringReader(ras.readUTF());
            config.load(sr);
        } catch (IOException e) {
            LOG.error("Cannot read file: {}", file.toAbsolutePath());
            throw e;
        }        

        LocalDateTime lastModified = LocalDateTime.from(
                Files.getLastModifiedTime(file).toInstant());
        
        LOG.trace("{} last activity: {}", file.toAbsolutePath(), lastModified);
        
        jsd.setLastActivity(lastModified);
        jsd.setProgress(config.getDouble("progress", 0d));
        jsd.setNote(config.getString("note", null));
        jsd.setStartTime(config.getLocalDateTime("startTime"));
        jsd.setEndTime(config.getLocalDateTime("endTime"));
        jsd.setStopRequested(config.getBoolean("stopRequested"));
        
        Properties props = jsd.getProperties();
        for (String key : config.keySet()) {
            if (key.startsWith(".")) {
                props.put(StringUtils.removeStart(".", key), props.get(key));
            }
        }
    }

    @Override
    public final void remove(final String suiteName, final String jobId)
            throws IOException {
        Path file = resolveDataFile(suiteName, jobId);
        FileUtil.delete(file.toFile());
    }

    //TODO have built-in methods to load backed-up sessions? 
    @Override
    public final void backup(final String suiteName,  
            final LocalDateTime backupDate) throws IOException {
        Path sDir = resolveDataDir(suiteName);
        Path bDir = resolveBackupDir(suiteName, backupDate);

        if (!atomicMoveFailed) {
            try {
                Files.move(sDir, bDir, StandardCopyOption.ATOMIC_MOVE);
                return;
            } catch (AtomicMoveNotSupportedException e) {
                atomicMoveFailed = true;
                LOG.error("Atomic file move is not supported by the file "
                        + "system. Will perform a regular move.");
            }
        }
        // fallback
        Files.move(sDir, bDir);
    }

    @Override
    public LocalDateTime touch(String suiteName, String jobId) 
            throws IOException {
        Path file = resolveDataFile(suiteName, jobId);
        Instant now = Instant.now();
        Files.setLastModifiedTime(file, FileTime.from(now));        
        return LocalDateTime.from(now);
    }
    
    
    private Path resolveDataFile(
            final String suiteName, final String jobId) {
        return resolveDataFile(suiteName, jobId, 0);
    }
    private Path resolveDataFile(
            final String suiteName, final String jobId, final int attemptNo) {
        String suffix = StringUtils.EMPTY;
        if (attemptNo > 0) {
            suffix = "." + Integer.toString(attemptNo);
        }
        return resolveDataDir(suiteName).resolve(
                FileUtil.toSafeFileName(jobId) + ".job" + suffix);
    }
    private Path resolveDataDir(final String suiteName) {
        return storeDir.resolve(Paths.get(FileUtil.toSafeFileName(suiteName)));
    }
    private Path resolveBackupDir(
            final String suiteName, final LocalDateTime backupDate) 
                    throws IOException {
        if (storeBackupDir == null) {
            return null;
        }
        return FileUtil.createDateFormattedDirs(storeBackupDir.resolve(
                FileUtil.toSafeFileName(suiteName)).toFile(), 
                        DateUtil.toDate(backupDate), 
                                "yyyy/MM/dd/HH-mm-ss").toPath();
    }

    @Override
    public void loadFromXML(Reader in) throws IOException {
        XMLConfiguration xml = XMLConfigurationUtil.newXMLConfiguration(in);
        String dir = null;
        
        dir = xml.getString("storeDir", null);
        if (dir != null) {
            setStoreDir(Paths.get(dir));
        }

        dir = xml.getString("storeBackupDir", null);
        if (dir != null) {
            setStoreBackupDir(Paths.get(dir));
        }
    }

    @Override
    public void saveToXML(Writer out) throws IOException {
        try {
            EnhancedXMLStreamWriter w = new EnhancedXMLStreamWriter(out);
            w.writeStartElement("store");
            w.writeAttribute("class", getClass().getCanonicalName());

            if (storeDir != null) {
                w.writeElementString("storeDir", 
                        storeDir.toAbsolutePath().toString());
            }
            if (storeBackupDir != null) {
                w.writeElementString("storeBackupDir", 
                        storeBackupDir.toAbsolutePath().toString());
            }
            w.writeEndElement();
            w.flush();
            w.close();
        } catch (XMLStreamException e) {
            throw new IOException("Cannot save as XML.", e);
        }       
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof FileJobSessionStore)) {
            return false;
        }
        FileJobSessionStore castOther = (FileJobSessionStore) other;
        return new EqualsBuilder()
                .append(storeDir, castOther.storeDir)
                .append(storeBackupDir, castOther.storeBackupDir)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(storeDir)
                .append(storeBackupDir)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("storeDir", storeDir)
                .append("storeBackupDir", storeBackupDir)
                .toString();
    }

    @Override
    public void accept(JefEvent event) {
        if (!event.equalsName(JefEvent.SUITE_STARTED)) {
            return;
        }

        Path dir = storeDir;
        if (storeDir == null) {
            dir = DEFAULT_STORE_PATH;
            LOG.error("JEF session store path cannot be null. "
                    + "Will use default: {}", dir);
        }
        try {
            Files.createDirectories(dir);
            LOG.info("Job session store directory: {}", dir.toAbsolutePath());
        } catch (IOException e) {
            throw new JefException("Cannot create session store directory: "
                    + dir.toAbsolutePath(), e);
        }
        
        if (storeBackupDir != null) {
            try {
                Files.createDirectories(storeBackupDir);
                LOG.info("Job session store backup directory: {}", 
                        storeBackupDir.toAbsolutePath());
            } catch (IOException e) {
                throw new JefException(
                        "Cannot create session store backup directory: "
                        + storeBackupDir.toAbsolutePath(), e);
            }
        } else {
            LOG.info("No job session store backup directory specified.");
        }
        
////            LOG.debug("Status serialization directory: " + path); 
//        jobdirLatest = path + File.separatorChar 
//                + "latest" + File.separatorChar + "status";
//        jobdirBackupBase = path + "/backup";
//        File dir = new File(jobdirLatest);
//        if (!dir.exists()) {
//            try {
//                FileUtils.forceMkdir(dir);
//            } catch (IOException e) {
//                throw new JEFException("Cannot create status directory: "
//                        + dir, e);
//            }
//        }
    }


}
