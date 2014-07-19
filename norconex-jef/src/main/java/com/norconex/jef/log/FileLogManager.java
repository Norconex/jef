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
package com.norconex.jef.log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import com.norconex.commons.lang.io.FileUtil;

/**
 * Log manager using the file system to store its logs.
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public class FileLogManager implements ILogManager {

    /** Directory where to store the file. */
    private final String logdirLatest;
    /** Directory where to backup the file. */
    private final File logdirBackup;
    /** Log4J layout to use for the generated log. */
    private final ThreadSafeLayout layout;
    
    private static final String LOG_SUFFIX = ".log";
    
    
    /**
     * Constructor.
     * @param logdir base directory where the log should be stored
     */
    public FileLogManager(final String logdir) {
        this(logdir, new PatternLayout(
                "%d{yyyy-MM-dd HH:mm:ss,SSS} [%t] %p %30.30c %x - %m\n"));
              
    }

    /**
     * Creates a new <code>FileLogManager</code>, wrapping the given
     * layout into a <code>ThreadSafeLayout</code>.
     * @param logdir base directory where the log should be stored
     * @param layout Log4J layout for rendering the logs
     */
    public FileLogManager(final String logdir, final Layout layout) {
        super();
        logdirLatest = logdir + "/latest";
        logdirBackup = new File(logdir + "/backup");
        this.layout = new ThreadSafeLayout(layout);
        File latestDir = new File(logdirLatest);
        if (!latestDir.exists()) {
            latestDir.mkdirs();
        }
        if (!logdirBackup.exists()) {
            logdirBackup.mkdirs();
        }
    }

    @Override
    public final Appender createAppender(final String namespace)
            throws IOException {
        return new FileAppender(
                layout, logdirLatest + "/" + namespace + LOG_SUFFIX);
    }
    
    @Override
    public final void backup(final String namespace, final Date backupDate)
            throws IOException {
        String date = new SimpleDateFormat(
                "yyyyMMddHHmmssSSSS").format(backupDate);
        File progressFile = new File(
                logdirLatest + "/" + namespace + LOG_SUFFIX);
        File backupFile =
            new File(FileUtil.createDateDirs(logdirBackup, backupDate)
                    + "/" + date + "__" + namespace + LOG_SUFFIX);
        if (!progressFile.renameTo(backupFile)) {
            throw new IOException("Could not move file from \""
                    + progressFile + "\" to \"" + backupFile + "\"");
        }
    }

    @Override
    public final InputStream getLog(final String namespace) throws IOException {
        File logFile = getLogFile(namespace);
        if (logFile != null && logFile.exists()) {
            return new FileInputStream(logFile);
        }
        return null;
    }
    @Override
    public InputStream getLog(String namespace,
            String jobId) throws IOException {
        if (jobId == null) {
            return getLog(namespace);
        }
        InputStream fullLog = getLog(namespace);
        if (fullLog != null) {
            return new FilteredInputStream(getLog(namespace), jobId);
        }
        return null;
    }

    /**
     * Gets the log file used by this log manager.
     * @param namespace log file namespace
     * @return log file
     */
    public File getLogFile(final String namespace) {
        if (namespace == null) {
            return null;
        }
        return new File(logdirLatest + "/" + namespace + LOG_SUFFIX);
    }
    
    //TODO consider using RegexFilteredInputStream
    private class FilteredInputStream extends InputStream {
    
        private final BufferedReader bufferedInput;
        private final String startsWith;
        private InputStream lineStream;
        private boolean closed = false;
    
        public FilteredInputStream(InputStream is, String startsWith)
                throws IOException {
            super();
            this.bufferedInput = new BufferedReader(
                    new InputStreamReader(is));
            this.startsWith = startsWith;
            nextLine();
        }
    
        @Override
        public int read() throws IOException {
            if (lineStream == null) {
                return -1;
            }
            int ch = lineStream.read();
            if (ch == -1) {
                if (!nextLine()) {
                    return -1;
                }
                return read();
            }
            return ch;
        }
    
        private boolean nextLine() throws IOException {
            if (lineStream != null) {
                lineStream.close();
                lineStream = null;
            }
            if (closed) {
                return false;
            }
            String line;
            while ((line = bufferedInput.readLine()) != null) {
                if (line.startsWith(startsWith + ":")) {
                    line += "\n";
                    lineStream = new ByteArrayInputStream(line.getBytes());
                    return true;
                }
            }
            bufferedInput.close();
            closed = true;
            return false;
        }
    }

}
