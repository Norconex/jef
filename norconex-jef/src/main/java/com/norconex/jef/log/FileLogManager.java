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
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
@SuppressWarnings("nls")
public class FileLogManager implements ILogManager {

    /** Directory where to store the file. */
    private final String logdirLatest;
    /** Directory where to backup the file. */
    private final File logdirBackup;
    /** Log4J layout to use for the generated log. */
    private final ThreadSafeLayout layout;
    
//    /**
//     * Constructor.
//     */
//    public FileLogManager() {
//        this(JefUtils.getDefaultWorkDir());
//    }

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

    /**
     * @see com.norconex.jef.log.ILogManager#createAppender(String)
     */
    public final Appender createAppender(final String namespace)
            throws IOException {
        return new FileAppender(
                layout, logdirLatest + "/" + namespace + ".log");
    }
    
    /**
     * @see com.norconex.jef.log.ILogManager#backup(String, Date)
     */
    public final void backup(final String namespace, final Date backupDate)
            throws IOException {
        String date = new SimpleDateFormat(
                "yyyyMMddHHmmssSSSS").format(backupDate);
        File progressFile = new File(logdirLatest + "/" + namespace + ".log");
        File backupFile =
            new File(FileUtil.createDateDirs(logdirBackup, backupDate)
                    + "/" + date + "__" + namespace + ".log");
        if (!progressFile.renameTo(backupFile)) {
            throw new RuntimeException("Could not move file from \""
                    + progressFile + "\" to \"" + backupFile + "\"");
        }
    }

    /**
     * @see com.norconex.jef.log.ILogManager#getLog(java.lang.String)
     */
    public final InputStream getLog(final String namespace) throws IOException {
        File logFile = getLogFile(namespace);
        if (logFile != null && logFile.exists()) {
            return new FileInputStream(logFile);
        }
        return null;
    }
    /**
     * @see com.norconex.jef.log.ILogManager#getLog(java.lang.String)
     */
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
        return new File(logdirLatest + "/" + namespace + ".log");
    }
    
    //TODO consider using RegexFilteredInputStream
    private class FilteredInputStream extends InputStream {
    
        private final BufferedReader bufferedInput;
        private final String startsWith;
        private InputStream lineStream;
        private boolean closed = false;
    
        /**
         * Constructor.
         * @throws IOException
         */
        public FilteredInputStream(InputStream is, String startsWith)
                throws IOException {
            super();
            this.bufferedInput = new BufferedReader(
                    new InputStreamReader(is));
            this.startsWith = startsWith;
            nextLine();
        }
    
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
